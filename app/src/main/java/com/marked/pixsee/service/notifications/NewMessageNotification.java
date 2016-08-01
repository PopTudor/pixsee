package com.marked.pixsee.service.notifications;

import android.app.Notification;
import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.data.Mapper;

/**
 * Created by Tudor on 22-Jul-16.
 */
class NewMessageNotification extends FcmNotification {
	public NewMessageNotification(RemoteMessage remoteMessage, Mapper<RemoteMessage, ?> mapper, Context context) {
		super(remoteMessage, mapper, context);
	}

	@Override
	public Notification buildNotification() {
		return null;
	}
}
