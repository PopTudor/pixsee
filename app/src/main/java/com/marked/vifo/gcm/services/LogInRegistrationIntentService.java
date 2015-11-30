/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marked.vifo.gcm.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.marked.vifo.R;
import com.marked.vifo.extras.IHTTPStatusCodes;
import com.marked.vifo.gcm.extras.IgcmConstants;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * To receive GCM messages, this app must register with GCM and get a unique identifier called a registration token.
 * If you are using the XMPP connection server, the client app can send "upstream" messages back to the app server.
 * This is needed by GCM but I will use it also to realise login,registration and password recovery
 * https://developers.google.com/cloud-messaging/android/client
 */
public class LogInRegistrationIntentService extends IntentService {
    // action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SIGNUP = "com.marked.vifo.gcm.services.action.ACTION_SIGNUP";
    private static final String ACTION_LOGIN = "com.marked.vifo.gcm.services.action.ACTION_LOGIN";
    private static final String ACTION_RECOVERY = "com.marked.vifo.gcm.services.action.RECOVERY";

    // Parameters send from outside, for example when clicked the login button and we send from there the email and password to
    // this service
    private static final String EXTRA_PARAM_EMAIL = "com.marked.vifo.gcm.services.extra.EMAIL";
    private static final String EXTRA_PARAM_NAME = "com.marked.vifo.gcm.services.extra.NAME";
    private static final String EXTRA_PARAM_PASSWORD = "com.marked.vifo.gcm.services.extra.PASSWORD";

    private static final String[] TOPICS = {"global"};

    private SharedPreferences mSharedPreferences;
    /* used to generate unique ID per instance of the app. This can be used in authentification
    * https://developers.google.com/instance-id/ */
    private InstanceID instanceID;
    private RequestQueue requestQueue;

    /*
    * One time initialization
    * */
    public LogInRegistrationIntentService() {
        super("RegIntentService");
    }

    /**
     * Starts this service to perform action Signup with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // helper method
    public static void startActionSignup(Context context,String name, String email, String password) {
        Intent intent = new Intent(context, LogInRegistrationIntentService.class);
        intent.setAction(ACTION_SIGNUP);
        intent.putExtra(EXTRA_PARAM_NAME, name);
        intent.putExtra(EXTRA_PARAM_EMAIL, email);
        intent.putExtra(EXTRA_PARAM_PASSWORD, password);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Signup with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // helper method
    public static void startActionLogin(Context context, String email, String password) {
        Intent intent = new Intent(context, LogInRegistrationIntentService.class);
        intent.setAction(ACTION_LOGIN);
        intent.putExtra(EXTRA_PARAM_EMAIL, email);
        intent.putExtra(EXTRA_PARAM_PASSWORD, password);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Signup with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // helper method
    public static void startActionRecovery(Context context, String email) {
        Intent intent = new Intent(context, LogInRegistrationIntentService.class);
        intent.setAction(ACTION_RECOVERY);
        intent.putExtra(EXTRA_PARAM_EMAIL, email);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        requestQueue = Volley.newRequestQueue(this);
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.

            instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_LOGIN.equals(action)) {
                    final String param1 = intent.getStringExtra(EXTRA_PARAM_EMAIL);
                    final String param2 = intent.getStringExtra(EXTRA_PARAM_PASSWORD);
                    handleActionLogin(param1, param2, token);
                } else if (ACTION_SIGNUP.equals(action)) {
                    final String param1 = intent.getStringExtra(EXTRA_PARAM_NAME);
                    final String param2 = intent.getStringExtra(EXTRA_PARAM_EMAIL);
                    final String param3 = intent.getStringExtra(EXTRA_PARAM_PASSWORD);
                    handleActionSignup(param1, param2, param3, token);
                } else if (ACTION_RECOVERY.equals(action)) {
                    final String param1 = intent.getStringExtra(EXTRA_PARAM_EMAIL);
                    handleActionRecovery(param1, token);
                }
            }

            // Subscribe to topic channels
            subscribeTopics(token);
            // [END register_for_gcm]
        } catch (IOException e) {

            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            mSharedPreferences.edit().putBoolean(IgcmConstants.SENT_TOKEN_TO_SERVER, false).apply();
        }


    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

    /**
     * Handle action Login in the provided background thread with the provided
     * parameters.
     * Persist registration to third-party servers.
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application. If the user already exists, update his registration token in case it's outdated
     *
     * @param email    The email of the account.
     * @param password The password to login
     * @param token    The new token.
     */
    private void handleActionLogin(String email, String password, String token) {
        try {
            /* Use URLEncoder to replace spaces with %20 or + and replace invalid URL chars with equivalent in hex*/
            String verifyUserURL = getVerifyUserURL(IgcmConstants.SERVER_USER_LOGIN, email, password, token);
            /* if sent_token_to_server == true, we are registered*/
            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, verifyUserURL, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // You should store a boolean that indicates whether the generated token has been
                    // sent to your server. If the boolean is false, send the token to your server,
                    // otherwise your server should have already received the token.
                    mSharedPreferences.edit().putBoolean(IgcmConstants.SENT_TOKEN_TO_SERVER, true).apply();
                    notifyBroadcastReceiver(IgcmConstants.ACTION_LOGIN);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSharedPreferences.edit().putBoolean(IgcmConstants.SENT_TOKEN_TO_SERVER, false).apply();
                    handleVolleyError(error);
                }
            });

            requestQueue.add(jsonRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle action Signup in the provided background thread with the provided
     * parameters.
     * Persist registration to third-party servers.
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param email    The email of the user we want to store.
     * @param password The password of the user.
     * @param token    The new token.
     */
    private void handleActionSignup(String name, String email, String password, String token) {
        try {
            /* Use URLEncoder to replace spaces with %20 or + and replace invalid URL chars with equivalent in hex*/
            String verifyUserURL = IgcmConstants.SERVER_USER + "?token=" + token +
                                   "&name=" + URLEncoder.encode(name, "UTF-8") +
                                   "&email=" + URLEncoder.encode(email, "UTF-8") +
                                   "&password=" + URLEncoder.encode(password, "UTF-8");
            /* if sent_token_to_server == true, we are registered*/
            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, verifyUserURL, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // You should store a boolean that indicates whether the generated token has been
                    // sent to your server. If the boolean is false, send the token to your server,
                    // otherwise your server should have already received the token.
                    mSharedPreferences.edit().putBoolean(IgcmConstants.SENT_TOKEN_TO_SERVER, true).apply();
                    notifyBroadcastReceiver(IgcmConstants.ACTION_SIGNUP);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSharedPreferences.edit().putBoolean(IgcmConstants.SENT_TOKEN_TO_SERVER, false).apply();
                    handleVolleyError(error);
                }
            });

            requestQueue.add(jsonRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle action Recovery in the provided background thread with the provided
     * parameters.
     * Persist registration to third-party servers.
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param email The email where we sent the new password
     * @param token The new token to update if we find the email of the user
     */
    private void handleActionRecovery(String email, String token) {
        try {
            /* Use URLEncoder to replace spaces with %20 or + and replace invalid URL chars with equivalent in hex*/
            String verifyUserURL = getVerifyUserURL(IgcmConstants.SERVER_USER_RECOVERY, email, "", token);
            /* if sent_token_to_server == true, we are registered*/
            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, verifyUserURL, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // You should store a boolean that indicates whether the generated token has been
                    // sent to your server. If the boolean is false, send the token to your server,
                    // otherwise your server should have already received the token.
                    mSharedPreferences.edit().putBoolean(IgcmConstants.SENT_TOKEN_TO_SERVER, true).apply();
                    notifyBroadcastReceiver(IgcmConstants.ACTION_RECOVERY);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSharedPreferences.edit().putBoolean(IgcmConstants.SENT_TOKEN_TO_SERVER, false).apply();
                    handleVolleyError(error);
                }
            });

            requestQueue.add(jsonRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify UI ProgressDialog that the login/signup/password recovery is done
     */
    private void notifyBroadcastReceiver(String action) {
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(action);
        LocalBroadcastManager.getInstance(LogInRegistrationIntentService.this).sendBroadcast(registrationComplete);
    }


    private void handleVolleyError(VolleyError error) {
        NetworkResponse networkResponse = error.networkResponse;
        Intent intent = new Intent(IgcmConstants.ACTION_ERROR);
        if (networkResponse != null) {
            Log.e("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode);
            intent.putExtra(IHTTPStatusCodes.ERROR_RESPONSE_STATUS_CODE, networkResponse.statusCode);
        }
        if (error instanceof TimeoutError) {
            Log.e("Volley", "TimeoutError");
            intent.putExtra(IHTTPStatusCodes.ERROR_RESPONSE_STATUS_CODE, IHTTPStatusCodes.REQUEST_TIMEOUT);
        } else if (error instanceof NoConnectionError) {
            Log.e("Volley", "NoConnectionError");
        } else if (error instanceof AuthFailureError) {
            Log.e("Volley", "AuthFailureError");
        } else if (error instanceof ServerError) {
            Log.e("Volley", "ServerError");
        } else if (error instanceof NetworkError) {
            Log.e("Volley", "NetworkError");
        } else if (error instanceof ParseError) {
            Log.e("Volley", "ParseError");
        }
        LocalBroadcastManager.getInstance(LogInRegistrationIntentService.this).sendBroadcast(intent);
    }

    private String getVerifyUserURL(String action, String email, String password, String token) throws UnsupportedEncodingException {
        return action + "?token=" + token + "&email=" +
               URLEncoder.encode(email, "UTF-8") + "&password=" +
               URLEncoder.encode(password, "UTF-8");
    }

}
