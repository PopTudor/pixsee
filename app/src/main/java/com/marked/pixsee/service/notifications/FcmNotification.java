package com.marked.pixsee.service.notifications;

import android.app.Notification;
import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.data.Mapper;

/**
 * Created by Tudor on 01-Aug-16.
 * If a new type of notification is needed you have to extend this class
 */
public abstract class FcmNotification {
	protected final RemoteMessage mRemoteMessage;
	protected final Mapper<RemoteMessage,?> mMapper;
	protected final Context mContext;

	public FcmNotification(RemoteMessage remoteMessage, Mapper<RemoteMessage, ?> mapper, Context context) {
		mRemoteMessage = remoteMessage;
		mMapper = mapper;
		mContext = context;
	}

	public abstract Notification buildNotification();
}
