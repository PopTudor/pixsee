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
import android.widget.Toast;

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
import com.marked.vifo.gcm.extras.IgcmConstants;

import org.json.JSONObject;

import java.io.IOException;

/**
 * To receive GCM messages, this app must register with GCM and get a unique identifier called a registration token.
 * If you are using the XMPP connection server, the client app can send "upstream" messages back to the app server.
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";

    private static final String[] TOPICS = {"global"};
    private Intent mIntent;
    private SharedPreferences mSharedPreferences;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mIntent = intent;
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            sendRegistrationToServer(token);
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
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        String email = mIntent.getStringExtra("email"), password = mIntent.getStringExtra("password");

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String verifyUserURL = IgcmConstants.SERVER_REGISTER_USER +"?token="+token+"&email=" + email + "&password=" + password;
        verifyUserURL = verifyUserURL.replaceAll(" ", "%20");

        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, verifyUserURL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                mSharedPreferences.edit().putBoolean(IgcmConstants.SENT_TOKEN_TO_SERVER, true).apply();
                // Notify UI that registration has completed, so the progress indicator can be hidden.
                Intent registrationComplete = new Intent(IgcmConstants.REGISTRATION_COMPLETE);
                registrationComplete.putExtra("response", "ok");
                LocalBroadcastManager.getInstance(RegistrationIntentService.this).sendBroadcast(registrationComplete);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e("Volley", "Error. HTTP Status Code:"+networkResponse.statusCode);
                }

                if (error instanceof TimeoutError) {
                    Log.e("Volley", "TimeoutError");
                }else if(error instanceof NoConnectionError){
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
//                Log.e("***","onErrorResponse "+error.getMessage());
                Toast.makeText(RegistrationIntentService.this," " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
