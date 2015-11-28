package com.marked.vifo.gcm;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;

import com.marked.vifo.R;
import com.marked.vifo.activities.ContactListActivity;
import com.marked.vifo.extras.IHTTPStatusCodes;
import com.marked.vifo.gcm.extras.IgcmConstants;

/**
 * Created by Tudor Pop on 28-Nov-15.
 */
public class RegistrationBroadcastReceiver extends BroadcastReceiver {
    private SharedPreferences mSharedPreferences;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout mContainer;
    Dismiss dismiss;

    public RegistrationBroadcastReceiver(Context context, Dismiss dialog,CoordinatorLayout container) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        mProgressDialog = dialog;
        mContainer = container;
        dismiss = dialog;
    }
public interface Dismiss{
    void dismissDialog();
}
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent toStart=null;
        if (intent != null){
            String action = intent.getAction();
            if (action.equals(IgcmConstants.ACTION_LOGIN)) {
                toStart = new Intent(context, ContactListActivity.class);
                toStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(toStart);
                dismiss.dismissDialog();
//                mProgressDialog.dismiss();
            } else if (action.equals(IgcmConstants.ACTION_SIGNUP)) {
                // TODO: 28-Nov-15 implement this
            } else if (action.equals(IgcmConstants.ACTION_RECOVERY)) {
                // TODO: 28-Nov-15 implement this
            } else if (action.equals(IgcmConstants.ACTION_ERROR)) {
//                mProgressDialog.dismiss();
                dismiss.dismissDialog();
                int errorStatusCode = intent.getIntExtra(IHTTPStatusCodes.ERROR_RESPONSE_STATUS_CODE, 0);
                actionError(context,errorStatusCode);
            }
        }
    }
    private void actionError(Context context,int errorStatusCode){
        Snackbar snackbar = null;
        switch (errorStatusCode) {
            case IHTTPStatusCodes.REQUEST_CONFLICT:
                snackbar = Snackbar.make(mContainer, "You already have an account", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            case IHTTPStatusCodes.REQUEST_TIMEOUT:
                snackbar = Snackbar.make(mContainer, "Timeout error", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            case IHTTPStatusCodes.REQUEST_INCORRECT_PASSWORD:
                snackbar = Snackbar.make(mContainer, "Incorrect password", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            case IHTTPStatusCodes.NOT_FOUND:
                snackbar = Snackbar.make(mContainer, "We are sorry, but we did not found you", Snackbar.LENGTH_LONG).setActionTextColor(ContextCompat.getColor(context, R.color.white));
                break;
        }
        if (snackbar != null) {
            snackbar.getView().setBackgroundColor(Color.WHITE);
            snackbar.show();
        }
    }
}
