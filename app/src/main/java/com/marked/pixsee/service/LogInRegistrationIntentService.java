/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marked.pixsee.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.marked.pixsee.friends.mapper.UserToCvMapper;
import com.marked.pixsee.utility.GCMConstants;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * To receive GCM messages, this app must register with GCM and get a unique identifier called a registration token.
 * If you are using the XMPP connection server, the client app can send "upstream" messages back to the app server.
 * This is needed by GCM but I will use it also to realise login,registration and password recovery
 * https://developers.google.com/cloud-messaging/android/client
 */
public class LogInRegistrationIntentService extends IntentService {
	private UserToCvMapper userToCvMapper = new UserToCvMapper();
	public LogInRegistrationIntentService() {
		super("RegIntentService");
	}

	public void onHandleIntent(Intent intent) {
		try {
			// [START register_for_gcm]
			// Initially this call goes out to the network to retrieve the token, subsequent calls
			// are local.
			// R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
			// See https://developers.google.com/cloud-messaging/android/start for details on this file.

			String scope = FirebaseMessaging.INSTANCE_ID_SCOPE;
			Bundle extras = null;
			getDefaultSharedPreferences(this).edit().putString(GCMConstants.TOKEN, FirebaseInstanceId.getInstance().getToken()).apply();

			// Subscribe to topic channels
			subscribeTopics(FirebaseInstanceId.getInstance().getToken());
			// [END register_for_gcm]
		} catch (IOException e) {
			// If an exception happens while fetching the new token or updating our registration data
			// on a third-party server, this ensures that we'll attempt the update at a later time.
			getDefaultSharedPreferences(LogInRegistrationIntentService.this).edit()
			                                                                .putBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, false)
			                                                                .apply();
		}
	}

	/**
	 * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
	 *
	 * @param token GCM token
	 *              *
	 * @throws IOException if unable to reach the GCM PubSub service
	 */
	// [START subscribe_topics]
	private void subscribeTopics(String token) throws IOException {
		for (String topic : LogInRegistrationIntentService.TOPICS) {
			FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + topic);
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
	 *              *
	 * @param token The new token to update if we find the email of the user
	 */
	private void handleActionRecovery(String email, String token) {
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

	private static List<String> TOPICS = Collections.singletonList("global");
}