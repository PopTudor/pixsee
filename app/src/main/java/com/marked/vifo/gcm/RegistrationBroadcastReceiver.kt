package com.marked.vifo.gcm;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.support.v4.content.IntentCompat
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.marked.vifo.extra.GCMConstants
import com.marked.vifo.extra.HTTPStatusCodes
import com.marked.vifo.extra.ServerConstants
import com.marked.vifo.model.contact.Contact
import com.marked.vifo.model.database.DatabaseContract
import com.marked.vifo.model.database.database
import com.marked.vifo.ui.activity.ContactListActivity
import org.jetbrains.anko.async
import org.jetbrains.anko.db.transaction
import org.jetbrains.anko.defaultSharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

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
//                if(action != GCMConstants.ACTION_SIGNUP) /* when a new user signs up, he doesn't have any friens so we don't make a useless request*/
//                    requestListFriends(context)
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
    val id = context.defaultSharedPreferences.getString(GCMConstants.USER_ID, "")
    if (id.isNotBlank()) {
        val retrofitBuilder = retrofit2.Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ServerConstants.SERVER)
                .build()
        val service = retrofitBuilder.create(com.marked.vifo.networking.FriendsAPI::class.java)
        service.listFriends(id).enqueue(object : Callback<JsonObject> {
            override fun onFailure(p0: Call<JsonObject>?, p1: Throwable?) { p1?.printStackTrace() }
            override fun onResponse(p0: Call<JsonObject>?, response: Response<JsonObject>) {
                if(response.isSuccess) {
                    val friends = response.body().getAsJsonArray("friends")
                    context.async() {
                        context.database.use {
                            transaction {
                                val gson = Gson()
                                friends.forEach {
                                    insertWithOnConflict(DatabaseContract.Contact.TABLE_NAME, null, gson.fromJson(it, Contact::class.java).toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
                                }
                            }
                        }
                    }
                }
            }
        })
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