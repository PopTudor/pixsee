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
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private Intent mIntent;
    private String email,password;
    private SharedPreferences mSharedPreferences;
    /* used to generate unique ID per instance of the app. This can be used in authentification
    * https://developers.google.com/instance-id/ */
    private InstanceID instanceID;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mIntent = intent;
        try {
            email = mIntent.getStringExtra("email");
            password = mIntent.getStringExtra("password");
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            sendTokenToServer(token);
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
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendTokenToServer(String token) throws UnsupportedEncodingException {
        // Add custom implementation, as needed.
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String verifyUserURL = IgcmConstants.SERVER_REGISTER_USER + "?token=" + token + "&email=" + URLEncoder.encode(email, "UTF-8")  + "&password=" + URLEncoder.encode(password, "UTF-8");


        /* if sent_token_to_server == true, we are registered*/
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, verifyUserURL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                mSharedPreferences.edit().putBoolean(IgcmConstants.SENT_TOKEN_TO_SERVER, true).apply();
                // Notify UI that registration has completed, so the progress indicator can be hidden.
                Intent registrationComplete = new Intent(IgcmConstants.SENT_TOKEN_TO_SERVER);
                registrationComplete.putExtra(IgcmConstants.SENT_TOKEN_TO_SERVER, true);
                LocalBroadcastManager.getInstance(RegistrationIntentService.this).sendBroadcast(registrationComplete);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSharedPreferences.edit().putBoolean(IgcmConstants.SENT_TOKEN_TO_SERVER, false).apply();
                NetworkResponse networkResponse = error.networkResponse;

                Intent registrationError = new Intent(IgcmConstants.SENT_TOKEN_TO_SERVER);
                registrationError.putExtra(IgcmConstants.SENT_TOKEN_TO_SERVER, false);
                if (networkResponse != null) {
                    Log.e("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode);
                    registrationError.putExtra(IHTTPStatusCodes.ERROR_RESPONSE_STATUS_CODE, networkResponse.statusCode);
                }
                if (error instanceof TimeoutError) {
                    Log.e("Volley", "TimeoutError");
                    registrationError.putExtra(IHTTPStatusCodes.ERROR_RESPONSE_STATUS_CODE, IHTTPStatusCodes.REQUEST_TIMEOUT);
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

                LocalBroadcastManager.getInstance(RegistrationIntentService.this).sendBroadcast(registrationError);
                //                Toast.makeText(RegistrationIntentService.this,
//                        " " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonRequest);
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
    // [END subscribe_topics]
}
