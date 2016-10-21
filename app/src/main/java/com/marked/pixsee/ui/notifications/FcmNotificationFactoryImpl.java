package com.marked.pixsee.ui.notifications;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.R;
import com.marked.pixsee.ui.notifications.mapper.RemoteMessageToMessageMapper;
import com.marked.pixsee.ui.notifications.mapper.RemoteMessageToUserMapper;

/**
 * Created by Tudor on 01-Aug-16.
 */
class FcmNotificationFactoryImpl implements GCMListenerService.FcmNotificationFactory {
	private final Context mContext;
	private final String FRIEND_REQUEST;
	private final String NEW_MESSAGE;


	FcmNotificationFactoryImpl(Context context) {
		mContext = context;
		FRIEND_REQUEST = context.getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION);
		NEW_MESSAGE = context.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION);
	}

	@Override
	public FcmNotification createNotification(RemoteMessage remoteMessage) {
		// all RemoteMessages have set in the data field an action. This action is set by the server
		String clickAction = remoteMessage.getData().get("click_action");
		if (clickAction.equals(FRIEND_REQUEST))
			return new FriendRequestNotification(mContext, new RemoteMessageToUserMapper().map(remoteMessage));
		else if (clickAction.equals(NEW_MESSAGE))
			return new NewMessageNotification(mContext, new RemoteMessageToMessageMapper().map(remoteMessage));
		else
			throw new InvalidNotificationException("The click_action in remoteMessage is not valid !");
	}

	static class InvalidNotificationException extends RuntimeException {
		InvalidNotificationException(String detailMessage) {
			super(detailMessage);
		}
	}
}
