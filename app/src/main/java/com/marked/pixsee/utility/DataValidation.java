package com.marked.pixsee.utility;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

import com.marked.pixsee.R;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Tudor Pop on 23-Jan-16.
 */
public class DataValidation {
	private final Context context;

	public DataValidation(@NotNull Context context) {
		this.context = context;
	}

	public boolean validate(@NotNull String email, @NotNull String password) {
		if (email.isEmpty()) {
			Toast.makeText(context, R.string.empty_email, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (password.isEmpty()) {
			Toast.makeText(context, R.string.empty_password, Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
			Toast.makeText(context, R.string.invalid_email, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}