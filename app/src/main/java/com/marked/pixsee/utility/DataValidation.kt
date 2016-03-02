package com.marked.pixsee.utility

import android.content.Context
import android.util.Patterns
import com.marked.pixsee.R
import org.jetbrains.anko.toast

/**
 * Created by Tudor Pop on 23-Jan-16.
 */
data class DataValidation(val context: Context) {
	fun validate(email: String, password: String): Boolean {
		if (email.isBlank()) {
			context.toast(R.string.empty_email)
			return false
		}
		if (password.isBlank()) {
			context.toast(R.string.empty_password)
			return false;
		}
		if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
			context.toast(R.string.invalid_email)
			return false;
		}
		return true;
	}
}