package com.marked.pixsee.main;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.R;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.entry.EntryActivity;
import com.marked.pixsee.friends.data.FriendConstants;

/**
 * Created by Tudor on 22-Jul-16.
 */
public class FriendRequestEvent {
	private RemoteMessage mRemoteMessage;
	private RemoteMessageToUserMapper mRemoteMessageToUserMapper = new RemoteMessageToUserMapper();

	public FriendRequestEvent(RemoteMessage remoteMessage) {
		mRemoteMessage = remoteMessage;
	}

	public Intent buildIntent(Activity activity) {
		User user = mRemoteMessageToUserMapper.map(mRemoteMessage);
		Intent resultIntent = new Intent(activity, EntryActivity.class);
		resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		resultIntent.setAction(activity.getString(R.string.FRIEND_REQUEST));
		resultIntent.putExtra(FriendConstants.ID, user.getUserID());
		resultIntent.putExtra(FriendConstants.NAME, user.getName());
		resultIntent.putExtra(FriendConstants.EMAIL, user.getEmail());
		resultIntent.putExtra(MessageConstants.FROM, user.getToken());
		resultIntent.putExtra(FriendConstants.ICON_URL, user.getIconUrl());
		resultIntent.putExtra(FriendConstants.USERNAME, user.getUsername());

		PendingIntent resultPendingIntent = PendingIntent.getActivity(activity, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat
				.Builder(activity)
				.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.FLAG_AUTO_CANCEL)
				.setAutoCancel(true)
				.setSmallIcon(R.drawable.pixsee_v2)
				.setContentTitle(mRemoteMessage.getNotification().getTitle())
				.setContentText(mRemoteMessage.getNotification().getBody());
		mBuilder.setContentIntent(resultPendingIntent);

		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(1, mBuilder.build());
		return resultIntent;
	}
}
