/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marked.vifo.gcm.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.gcm.GcmPubSub
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.marked.vifo.R
import com.marked.vifo.extra.GCMConstants
import com.marked.vifo.extra.HTTPStatusCodes
import com.marked.vifo.extra.ServerConstants
import com.marked.vifo.helper.saveToTable
import com.marked.vifo.model.database.DatabaseContract
import com.marked.vifo.networking.LoginAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * To receive GCM messages, this app must register with GCM and get a unique identifier called a registration token.
 * If you are using the XMPP connection server, the client app can send "upstream" messages back to the app server.
 * This is needed by GCM but I will use it also to realise login,registration and password recovery
 * https://developers.google.com/cloud-messaging/android/client
 */
class LogInRegistrationIntentService : IntentService("RegIntentService") {

    private var mSharedPreferences: SharedPreferences? = null

    override fun onHandleIntent(intent: Intent?) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.

            val instanceID = InstanceID.getInstance(this)
            val scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE
            val extras: Bundle? = null
            val token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), scope, extras)

            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(GCMConstants.TOKEN, token).apply()

            if (intent != null) {
                val action = intent.action
                if (ACTION_LOGIN == action) {
                    val param1 = intent.getStringExtra(EXTRA_PARAM_EMAIL)
                    val param2 = intent.getStringExtra(EXTRA_PARAM_PASSWORD)
                    handleActionLogin(param1, param2, token)
                } else if (ACTION_SIGNUP == action) {
                    val param1 = intent.getStringExtra(EXTRA_PARAM_NAME)
                    val param2 = intent.getStringExtra(EXTRA_PARAM_EMAIL)
                    val param3 = intent.getStringExtra(EXTRA_PARAM_PASSWORD)
                    handleActionSignup(param1, param2, param3, token)
                } else if (ACTION_RECOVERY == action) {
                    val param1 = intent.getStringExtra(EXTRA_PARAM_EMAIL)
                    handleActionRecovery(param1, token)
                }
            }

            // Subscribe to topic channels
            subscribeTopics(token)
            // [END register_for_gcm]
        } catch (e: IOException) {

            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            mSharedPreferences!!.edit().putBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, false).apply()
        }


    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.

     * @param token GCM token
     * *
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    @Throws(IOException::class)
    private fun subscribeTopics(token: String) {
        val pubSub = GcmPubSub.getInstance(this)
        for (topic in TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null)
        }
    }

    /**
     * Handle action Login in the provided background thread with the provided
     * parameters.
     * Persist registration to third-party servers.
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application. If the user already has an account, update his registration token in case it's outdated

     * @param email    The email of the account.
     * *
     * @param password The password to login
     * *
     * @param token    The new token.
     */
    private fun handleActionLogin(email: String, password: String, token: String) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder().baseUrl(ServerConstants.SERVER).addConverterFactory(GsonConverterFactory.create()).client(httpClient).build()
        val service = retrofit.create(LoginAPI::class.java)
        service.login(email, password, token).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccess) {
                    /* You should store a boolean that indicates whether the generated token has been
                             sent to your server. If the boolean is false, send the token to your server,
                             otherwise your server should have already received the token.
                             */
                    val userID = response.body().get(GCMConstants.USER_ID).asString
                    val friends: JsonArray = if (response.body().get(GCMConstants.FRIENDS).asJsonArray == null) JsonArray() else response.body().get(GCMConstants.FRIENDS).asJsonArray
                    mSharedPreferences!!.edit().putBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, true).apply()/* if sent_token_to_server == true, we are registered*/
                    mSharedPreferences!!.edit().putString(GCMConstants.USER_ID, userID).apply()
                    notifyBroadcastReceiver(GCMConstants.ACTION_LOGIN)
                    getApplicationContext().saveToTable(DatabaseContract.Contact.TABLE_NAME, friends);
                } else {
                    handleVolleyError(response.raw().code())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * Handle action Signup in the provided background thread with the provided
     * parameters.
     * Persist registration to third-party servers.
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.

     * @param email    The email of the user we want to store.
     * *
     * @param password The password of the user.
     * *
     * @param token    The new token.
     */
    private fun handleActionSignup(name: String, email: String, password: String, token: String) {
        val logging = HttpLoggingInterceptor()
        // set your desired log level
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder().addInterceptor(logging)
        val retrofit = Retrofit.Builder().baseUrl(ServerConstants.SERVER).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build()
        retrofit.create(LoginAPI::class.java).create(name, email, token, password).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccess) {
                    // You should store a boolean that indicates whether the generated token has been
                    // sent to your server. If the boolean is false, send the token to your server,
                    // otherwise your server should have already received the token.
                    mSharedPreferences!!.edit().putBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, true).apply()/* if sent_token_to_server == true, we are registered*/
                    mSharedPreferences!!.edit().putString(GCMConstants.USER_ID, response.body().get(GCMConstants.USER_ID).asString).apply()
                    notifyBroadcastReceiver(GCMConstants.ACTION_SIGNUP)
                } else {
                    handleVolleyError(response.raw().code())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * Handle action Recovery in the provided background thread with the provided
     * parameters.
     * Persist registration to third-party servers.
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.

     * @param email The email where we sent the new password
     * *
     * @param token The new token to update if we find the email of the user
     */
    private fun handleActionRecovery(email: String, token: String) {
        //		try {
        //            /* Use URLEncoder to replace spaces with %20 or + and replace invalid URL chars with equivalent in hex*/
        //			String verifyUserURL = getVerifyUserURL(ServerConstants.SERVER_USER_RECOVERY, email, "", token);
        //            /* if sent_token_to_server == true, we are registered*/
        //			JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, verifyUserURL, new Response.Listener<JSONObject>() {
        //				@Override
        //				public void onResponse(JSONObject response) {
        //					// You should store a boolean that indicates whether the generated token has been
        //					// sent to your server. If the boolean is false, send the token to your server,
        //					// otherwise your server should have already received the token.
        //					mSharedPreferences.edit().putBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, true).apply();
        //					notifyBroadcastReceiver(GCMConstants.ACTION_RECOVERY);
        //				}
        //			}, new Response.ErrorListener() {
        //				@Override
        //				public void onErrorResponse(VolleyError error) {
        //					mSharedPreferences.edit().putBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, false).apply();
        //					handleVolleyError(error);
        //				}
        //			});
        //
        //			requestQueue.add(jsonRequest);
        //		} catch (UnsupportedEncodingException e) {
        //			e.printStackTrace();
        //		}
    }

    /**
     * Notify UI ProgressDialog that the login/signup/password recovery is done
     */
    private fun notifyBroadcastReceiver(action: String) {
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        val registrationComplete = Intent(action)
        LocalBroadcastManager.getInstance(this@LogInRegistrationIntentService).sendBroadcast(registrationComplete)
    }


    private fun handleVolleyError(error: Int) {
        mSharedPreferences!!.edit().putBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, false).apply()
        val intent = Intent(GCMConstants.ACTION_ERROR)
        intent.putExtra(HTTPStatusCodes.ERROR_RESPONSE_STATUS_CODE, error)
        LocalBroadcastManager.getInstance(this@LogInRegistrationIntentService).sendBroadcast(intent)
    }

    companion object {
        // action names that describe tasks that this
        // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
        private val ACTION_SIGNUP = "com.marked.vifo.gcm.services.action.ACTION_SIGNUP"
        private val ACTION_LOGIN = "com.marked.vifo.gcm.services.action.ACTION_LOGIN"
        private val ACTION_RECOVERY = "com.marked.vifo.gcm.services.action.RECOVERY"

        // Parameters send from outside, for example when clicked the login button and we send from there the email and password to
        // this service
        private val EXTRA_PARAM_EMAIL = "com.marked.vifo.gcm.services.extra.EMAIL"
        private val EXTRA_PARAM_NAME = "com.marked.vifo.gcm.services.extra.NAME"
        private val EXTRA_PARAM_PASSWORD = "com.marked.vifo.gcm.services.extra.PASSWORD"

        private val TOPICS = arrayOf("global")

        /**
         * Starts this service to perform action Signup with the given parameters. If
         * the service is already performing a task this action will be queued.

         * @see IntentService
         */
        // helper method
        fun startActionSignup(context: Context, name: String, email: String, password: String) {
            val intent = Intent(context, LogInRegistrationIntentService::class.java)
            intent.action = ACTION_SIGNUP
            intent.putExtra(EXTRA_PARAM_NAME, name)
            intent.putExtra(EXTRA_PARAM_EMAIL, email)
            intent.putExtra(EXTRA_PARAM_PASSWORD, password)
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Signup with the given parameters. If
         * the service is already performing a task this action will be queued.

         * @see IntentService
         */
        // helper method
        fun startActionLogin(context: Context, email: String, password: String) {
            val intent = Intent(context, LogInRegistrationIntentService::class.java)
            intent.action = ACTION_LOGIN
            intent.putExtra(EXTRA_PARAM_EMAIL, email)
            intent.putExtra(EXTRA_PARAM_PASSWORD, password)
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Signup with the given parameters. If
         * the service is already performing a task this action will be queued.

         * @see IntentService
         */
        // helper method
        fun startActionRecovery(context: Context, email: String) {
            val intent = Intent(context, LogInRegistrationIntentService::class.java)
            intent.action = ACTION_RECOVERY
            intent.putExtra(EXTRA_PARAM_EMAIL, email)
            context.startService(intent)
        }
    }
}/*
    * One time initialization
    * */
