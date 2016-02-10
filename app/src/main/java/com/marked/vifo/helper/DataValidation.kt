package com.marked.vifo.helper

import android.content.Context
import android.util.Patterns

/**
 * Created by Tudor Pop on 23-Jan-16.
 */
data class DataValidation(val context: Context, val email: String, val password: String) {
	fun validate(): Boolean {
		if (email.isNullOrBlank()) {
			context.Toast("The email field is empty")
			return false
		}
		if (password.isNullOrBlank()) {
			context.Toast("The password field is empty")
			return false;
		}
		if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
			context.Toast("You must enter a valid email")
			return false;
		}
		return true;
	}
}