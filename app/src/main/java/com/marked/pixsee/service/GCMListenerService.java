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

package com.marked.pixsee.service;

import android.app.Notification;
import android.app.NotificationManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.service.notifications.FcmNotification;
import com.marked.pixsee.service.notifications.FcmNotificationFactoryImpl;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Receives messages sent by GCM server
 */
public class GCMListenerService extends FirebaseMessagingService {
	private static final String TAG = "MyGcmListenerService";
	private FcmNotificationFactory mFcmNotificationFactory;
	private NotificationManager mNotificationManager;

	@Override
	public void onCreate() {
		super.onCreate();
		mFcmNotificationFactory = new FcmNotificationFactoryImpl(this);
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public void onMessageReceived(final RemoteMessage remoteMessage) {
		Observable.just(remoteMessage)
				.map(new Func1<RemoteMessage, FcmNotification>() {
					@Override
					public FcmNotification call(RemoteMessage s) {
						return mFcmNotificationFactory.createNotification(remoteMessage);
					}
				})
				.map(new Func1<FcmNotification, Notification>() {
					@Override
					public Notification call(FcmNotification fcmNotificationFactory) {
						return fcmNotificationFactory.buildNotification();
					}
				})
				.subscribe(new Action1<Notification>() {
					@Override
					public void call(Notification notification) {
						mNotificationManager.notify(0, notification);
					}
				});

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
//		if (!ChatFragment.isInForeground)
//			sendNotification(remoteMessage);
		// [END_EXCLUDE]
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

	/**
	 * Factory to produce FcmNotifications
	 */
	public interface FcmNotificationFactory {
		FcmNotification createNotification(RemoteMessage remoteMessage);
	}
}
