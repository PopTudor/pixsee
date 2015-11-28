package com.marked.vifo.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.IntentCompat;

import com.marked.vifo.activities.ContactListActivity;
import com.marked.vifo.extras.IHTTPStatusCodes;
import com.marked.vifo.gcm.extras.IgcmConstants;

/**
 * Created by Tudor Pop on 28-Nov-15.
 */
public class RegistrationBroadcastReceiver extends BroadcastReceiver {
    private RegistrationBroadcastReceiverListener registrationBroadcastReceiverListener;

    public void setOnRegistrationBroadcastReceiverListener(RegistrationBroadcastReceiverListener registrationBroadcastReceiverListener) {
        this.registrationBroadcastReceiverListener = registrationBroadcastReceiverListener;
    }

    /**
     * If we have an indeterminate ProgressDialog loading and we want to dismiss it
     * when we RegistrationBroadcastReceiver receives a signal(Login,signup,recovery)
     * we will make the Activity/entity that owns the dialog to dismiss it if it has one.
     */
    public interface RegistrationBroadcastReceiverListener {

        /**
         * Used to dismiss an indeterminate loading of a ProgressDialog
         */
        void dismissDialog();

        /**
         * This needs to be implemented in the Activity/Fragment that launches the RegistrationBroadcastReceiver
         * because we want to make a SnackBar if occurs some kind of error and you can not create UI elements
         * inside a BroadcastReceiver
         * @param errorStatusCode the error code sent by the server
         */
        void actionError(int errorStatusCode);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent toStart;
        if (intent != null){
            String action = intent.getAction();
            if (action.equals(IgcmConstants.ACTION_LOGIN)) {
                toStart = new Intent(context, ContactListActivity.class);
                toStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(toStart);
                if(registrationBroadcastReceiverListener != null)
                    registrationBroadcastReceiverListener.dismissDialog();
            } else if (action.equals(IgcmConstants.ACTION_SIGNUP)) {
                // TODO: 28-Nov-15 implement this
            } else if (action.equals(IgcmConstants.ACTION_RECOVERY)) {
                // TODO: 28-Nov-15 implement this
            } else if (action.equals(IgcmConstants.ACTION_ERROR)) {
                int errorStatusCode = intent.getIntExtra(IHTTPStatusCodes.ERROR_RESPONSE_STATUS_CODE, 0);
                if (registrationBroadcastReceiverListener != null) {
                    registrationBroadcastReceiverListener.dismissDialog();
                    registrationBroadcastReceiverListener.actionError(errorStatusCode);
                }
            }
        }
    }
}
