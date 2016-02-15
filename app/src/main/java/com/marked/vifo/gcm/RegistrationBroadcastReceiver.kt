package com.marked.vifo.gcm;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.support.v4.content.IntentCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.marked.vifo.extra.GCMConstants
import com.marked.vifo.extra.HTTPStatusCodes
import com.marked.vifo.extra.ServerConstants
import com.marked.vifo.model.RequestQueueAccess
import com.marked.vifo.model.contact.contactListfromJSONArray
import com.marked.vifo.model.database.DatabaseContract
import com.marked.vifo.model.database.database
import com.marked.vifo.model.requestQueue
import com.marked.vifo.ui.activity.ContactListActivity
import org.jetbrains.anko.async
import org.jetbrains.anko.db.transaction
import org.jetbrains.anko.defaultSharedPreferences
import org.json.JSONObject

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
                requestListFriends(context)
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
private fun requestListFriends(context: Context) {
    val id: String? = context.defaultSharedPreferences.getString(GCMConstants.USER_ID, null)
    if (id != null) {
        val request = JsonObjectRequest(Request.Method.GET,
                "${ServerConstants.SERVER_USER_FRIENDS}?id=$id",
                Response.Listener<JSONObject> { response -> // TODO: 12-Dec-15 add empty view)
                    val friends = response.getJSONArray("friends")
                    val friendsArray = friends.contactListfromJSONArray() // todo this loads all 10.000 contacts in memory. !not good
                    context.async() {
                        context.database.use {
                            transaction {
                                friendsArray.forEach {
                                    insertWithOnConflict(DatabaseContract.Contact.TABLE_NAME, null, it.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
                                }
                            }
                        }
                    }
                },
                Response.ErrorListener {
                    Log.d("***", "Error")
                })
        request.retryPolicy = DefaultRetryPolicy(1000 * 15, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        context.requestQueue.queue.add(request).tag = RequestQueueAccess.FRIENDS_TAG
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