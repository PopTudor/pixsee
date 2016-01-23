package com.marked.vifo.delegate

import android.app.ProgressDialog
import android.content.Context
import com.marked.vifo.extra.HTTPStatusCodes
import com.marked.vifo.gcm.RegistrationListener
import com.marked.vifo.helper.Toast

/**
 * Created by Tudor Pop on 23-Jan-16.
 */
class DialogRegistration(context: Context, progressDialog: ProgressDialog) : RegistrationListener {
	private val progressDialog = progressDialog
	private val context = context

	override fun onDismiss() {
		progressDialog.dismiss()
	}

	override fun onError(errorStatusCode: Int) {
		when (errorStatusCode) {
			HTTPStatusCodes.REQUEST_CONFLICT -> context.Toast("You already have an account")
			HTTPStatusCodes.REQUEST_TIMEOUT -> context.Toast("Timeout error")
			HTTPStatusCodes.REQUEST_INCORRECT_PASSWORD -> context.Toast("Incorrect password")
			HTTPStatusCodes.NOT_FOUND -> context.Toast("We are sorry, but we did not found you")
		}
	}
}