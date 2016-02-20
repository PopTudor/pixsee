package com.marked.vifo.gcm;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.IntentCompat
import com.marked.vifo.extra.GCMConstants
import com.marked.vifo.extra.HTTPStatusCodes
import com.marked.vifo.ui.activity.ContactListActivity

/**
 * Created by Tudor Pop on 28-Nov-15.
 */
class RegistrationBroadcastReceiver(val registrationListener: RegistrationListener?) : BroadcastReceiver() {

    constructor() : this(null)

    override
    fun onReceive(context: Context, intent: Intent) {
        val toStart = Intent(context, ContactListActivity::class.java);
        val action = intent.action;
        when (action) {
            GCMConstants.ACTION_LOGIN, GCMConstants.ACTION_SIGNUP -> {
                toStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                toStart.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(toStart);
                registrationListener?.onDismiss();
            }
            GCMConstants.ACTION_RECOVERY -> {

            }
            GCMConstants.ACTION_ERROR -> {
                val errorStatusCode = intent.getIntExtra(HTTPStatusCodes.ERROR_RESPONSE_STATUS_CODE, 0);
                registrationListener?.onDismiss();
                registrationListener?.onError(errorStatusCode);
            }
        }
    }
}



/**
 * If we have an indeterminate ProgressDialog loading and we want to dismiss it
 * when we RegistrationBroadcastReceiver receives a signal(Login,Signup,Recovery)
 * we will make the Activity/entity that owns the dialog to dismiss it(for us) if it has one.
 */
interface RegistrationListener {
    /**
     * Used to dismiss an indeterminate loading of a ProgressDialog
     */
    fun onDismiss();

    /**
     * This needs to be implemented in the Activity/Fragment that launches the RegistrationBroadcastReceiver
     * because we want to make a SnackBar if occurs some kind of error and you can not create UI elements
     * inside a BroadcastReceiver
     *
     * @param errorStatusCode the error code sent by the server
     */
    fun onError(errorStatusCode: Int);
}