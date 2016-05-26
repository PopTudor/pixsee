package com.marked.pixsee.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.IntentCompat;

import com.marked.pixsee.main.MainActivity;
import com.marked.pixsee.networking.HTTPStatusCodes;
import com.marked.pixsee.utility.GCMConstants;

import javax.inject.Inject;

/**
 * Created by Tudor Pop on 28-Nov-15.
 */
public class RegistrationBroadcastReceiver extends BroadcastReceiver {
	private RegistrationListener registrationListener;

	@Inject
	public RegistrationBroadcastReceiver(RegistrationListener registrationListener) {
		this.registrationListener = registrationListener;
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		Intent toStart = new Intent(context, MainActivity.class);
		String action = intent.getAction();
		if (action.equals(GCMConstants.ACTION_LOGIN) || action.equals(GCMConstants.ACTION_SIGNUP)) {
			toStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			toStart.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
			context.startActivity(toStart);
			registrationListener.onDismiss();
		} else if (action.equals(GCMConstants.ACTION_RECOVERY)) {

		} else if (action.equals(GCMConstants.ACTION_ERROR)) {
			int errorStatusCode = intent.getIntExtra(HTTPStatusCodes.ERROR_RESPONSE_STATUS_CODE, 0);
			registrationListener.onDismiss();
			registrationListener.onError(errorStatusCode);
		}
	}

	/**
	 * If we have an indeterminate ProgressDialog loading and we want to dismiss it
	 * when we RegistrationBroadcastReceiver receives a signal(Login,Signup,Recovery)
	 * we will make the Activity/entity that owns the dialog to dismiss it(for us) if it has one.
	 */
	public interface RegistrationListener {
		/**
		 * Used to dismiss an indeterminate loading of a ProgressDialog
		 */
		void onDismiss();

		/**
		 * This needs to be implemented in the Activity/Fragment that launches the RegistrationBroadcastReceiver
		 * because we want to make a SnackBar if occurs some kind of error and you can not create UI elements
		 * inside a BroadcastReceiver
		 *
		 * @param errorStatusCode the error code sent by the server
		 */
		void onError(int errorStatusCode);
	}
}


