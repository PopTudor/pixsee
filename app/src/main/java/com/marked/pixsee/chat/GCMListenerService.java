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

package com.marked.pixsee.chat;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Receives messages sent by GCM server
 */
public class GCMListenerService extends FirebaseMessagingService {

	private static final String TAG = "MyGcmListenerService";
	private static List<Callback> mCallbacks=new ArrayList<>(3);

	public static void addCallback(Callback callback) {
		mCallbacks.add(callback);
	}
	public static void clear(){mCallbacks.clear();}

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		for (Callback it:mCallbacks)
			it.receiveRemoteMessage(remoteMessage);

		if (remoteMessage.getFrom().startsWith("/topics/")) {
			// message received from some topic.
		} else {
			// normal downstream message.
		}

		/**
		 * Production applications would usually process the message here.
		 * Eg: - Syncing with server.
		 *     - Store message in local database.
		 *     - Update UI.
		 */

		/*send notification only if the user is not inside the chatting fragment */
		if (!ChatFragment.isInForeground)
			sendNotification(new Bundle());
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
//			Intent intent = new Intent(this, EntryActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			PendingIntent pendingIntent = getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
//			Uri defaultSoundUri = getDefaultUri(TYPE_NOTIFICATION);
//			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_chat_24dp)
//			                                                                                     .setWhen(0)
//			                                                                                     .setLargeIcon(BitmapFactory
//					                                                             .decodeResource(getResources(), R.mipmap.ic_launcher))
//			                                                                                     .setContentTitle(bundle.getString(NOTIFICATION_PAYLOAD_TITLE))
//			                                                                                     .setContentText(bundle.getString(NOTIFICATION_PAYLOAD_BODY))
//			                                                                                     .setAutoCancel(true)
//			                                                                                     .setSound(defaultSoundUri)
//			                                                                                     .setContentIntent(pendingIntent);

			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//			notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
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
	/* called when there was an error sending the upstream message */
	@Override
	public void onSendError(String s, Exception e) {
		super.onSendError(s, e);
		e.printStackTrace();
	}

	public interface Callback {
		void receiveRemoteMessage(RemoteMessage message);
	}

}