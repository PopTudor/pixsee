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

package com.marked.vifo.gcm.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.marked.vifo.R;
import com.marked.vifo.ui.activity.EntryActivity;
import com.marked.vifo.ui.fragment.ContactDetailFragment;

import static android.app.PendingIntent.getActivity;
import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.getDefaultUri;
import static com.marked.vifo.extra.MessageConstants.DATA_BODY;
import static com.marked.vifo.extra.MessageConstants.NOTIFICATION_PAYLOAD_BODY;
import static com.marked.vifo.extra.MessageConstants.NOTIFICATION_PAYLOAD_TITLE;

/**
 * Receives messages sent by GCM server
 */
public class GCMListenerService extends GcmListenerService {

	private static final String TAG = "MyGcmListenerService";
	static Callbacks mCallbacks;

	public static void setCallbacks(Callbacks callbacks) {
		mCallbacks = callbacks;
	}

	/**
	 * Called when message is received.
	 *
	 * @param from    SenderID of the app.
	 * @param payload Data bundle containing message data as key/value pairs.
	 *                For Set of keys use data.keySet().
	 */
	@Override
	public void onMessageReceived(String from, Bundle payload) {
		String text = payload.getString(DATA_BODY);
		Log.d(TAG, "Message: " + text);
		for (String s : payload.keySet())
			Log.d("***", "onMessageReceived " + s);

		if (mCallbacks != null)
			mCallbacks.receiveMessage(from, payload);

		if (from.startsWith("/topics/")) {
			// message received from some topic.
		} else {
			// normal downstream message.
		}

		// [START_EXCLUDE]
		/**
		 * Production applications would usually process the message here.
		 * Eg: - Syncing with server.
		 *     - Store message in local database.
		 *     - Update UI.
		 */

		/*send notification only if the user is not inside the chatting fragment */
		if (!ContactDetailFragment.Static.isInForeground())
			sendNotification(payload);
		// [END_EXCLUDE]
	}


	/**
	 * Create and show a simple notification containing the received GCM message.
	 *
	 * @param bundle GCM message received.
	 */
	private void sendNotification(Bundle bundle) {
		//		Bundle[{text=h, links=[null], notification=Bundle[{e=1, body=This is a notification that will be displayed ASAP., icon=ic_launcher, title=Hello, World}], collapse_key=com.marked.vifo}]
		if (bundle != null) {
			Intent intent = new Intent(this, EntryActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pendingIntent = getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
			Uri defaultSoundUri = getDefaultUri(TYPE_NOTIFICATION);
			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_chat_24dp)
			                                                                                     .setWhen(0)
			                                                                                     .setLargeIcon(BitmapFactory
					                                                             .decodeResource(getResources(), R.mipmap.ic_launcher))
			                                                                                     .setContentTitle(bundle.getString(NOTIFICATION_PAYLOAD_TITLE))
			                                                                                     .setContentText(bundle.getString(NOTIFICATION_PAYLOAD_BODY))
			                                                                                     .setAutoCancel(true)
			                                                                                     .setSound(defaultSoundUri)
			                                                                                     .setContentIntent(pendingIntent);

			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
		}
	}

	/**
	 * Called when an upstream message has been successfully sent to the GCM connection server.
	 *
	 * @param msgId of the upstream message sent using send(String, String, Bundle).
	 */
	@Override
	public void onMessageSent(String msgId) {
		super.onMessageSent(msgId);
	}

	/**
	 * Called when there was an error sending an upstream message.
	 *
	 * @param msgId of the upstream message sent using send(String, String, Bundle).
	 * @param error description of the error.
	 */
	@Override
	public void onSendError(String msgId, String error) {
		super.onSendError(msgId, error);
	}

	public interface Callbacks {
		void receiveMessage(String from, Bundle data);
	}

}
