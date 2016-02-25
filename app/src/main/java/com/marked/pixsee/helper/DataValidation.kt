package com.marked.pixsee.helper

import android.content.Context
import android.util.Patterns
import org.jetbrains.anko.toast

/**
 * Created by Tudor Pop on 23-Jan-16.
 */
data class DataValidation(val context: Context, val email: String, val password: String) {
	fun validate(): Boolean {
		if (email.isNullOrBlank()) {
			context.toast("The email field is empty")
			return false
		}
		if (password.isNullOrBlank()) {
			context.toast("The password field is empty")
			return false;
		}
		if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
			context.toast("You must enter a valid email")
			return false;
		}
		return true;
	}
}