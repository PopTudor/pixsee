package com.marked.pixsee.main;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.R;
import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.service.GCMListenerService;

/**
 * Created by Tudor on 22-Jul-16.
 */
public class FriendRequestNotification implements GCMListenerService.FcmEvent {
	public static final String FRIEND_REQUEST_TAG = "com.marked.pixsee.FRIEND_REQUEST";
	private final RemoteMessage mRemoteMessage;
	private final Mapper<RemoteMessage, User> mMessageToUserMapper;
	private final Context mContext;

	public FriendRequestNotification(RemoteMessage remoteMessage, Mapper<RemoteMessage, User> messageToUserMapper, Context context) {
		mRemoteMessage = remoteMessage;
		mMessageToUserMapper = messageToUserMapper;
		mContext = context;
	}

	@Override
	public Notification buildNotification() {
		User user = mMessageToUserMapper.map(mRemoteMessage);

		Intent resultIntent = new Intent(mContext, MainActivity.class);
		resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		resultIntent.setAction(FRIEND_REQUEST_TAG);
		resultIntent.putExtra(FRIEND_REQUEST_TAG, user);

		// open activity
		PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat
				.Builder(mContext)
				.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.FLAG_AUTO_CANCEL | Notification.PRIORITY_HIGH)
				.setAutoCancel(true)
				.setSmallIcon(R.drawable.pixsee_v2)
				.setContentIntent(resultPendingIntent)
				.setContentTitle("Friend Request")
				.setContentText("You received a friend request from " + user.getUsername());
		return mBuilder.build();
	}

}
