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
import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.entry.EntryActivity;
import com.marked.pixsee.service.GCMListenerService;

/**
 * Created by Tudor on 22-Jul-16.
 */
public class FriendRequestEvent implements GCMListenerService.FcmEvent {
	private RemoteMessage mRemoteMessage;
	private Mapper<RemoteMessage, User> mMessageToUserMapper;

	public FriendRequestEvent(RemoteMessage remoteMessage, Mapper<RemoteMessage, User> messageToUserMapper) {
		mRemoteMessage = remoteMessage;
		mMessageToUserMapper = messageToUserMapper;
	}

	public Intent buildIntent(Activity activity) {
		User user = mMessageToUserMapper.map(mRemoteMessage);

		Intent resultIntent = new Intent(activity, EntryActivity.class);
		resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		resultIntent.setAction(activity.getString(R.string.FRIEND_REQUEST));
		resultIntent.putExtra(User.TAG, user);


		PendingIntent resultPendingIntent = PendingIntent.getActivity(activity, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat
				.Builder(activity)
				.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.FLAG_AUTO_CANCEL | Notification.PRIORITY_HIGH)
				.setAutoCancel(true)
				.setSmallIcon(R.drawable.pixsee_v2)
				.setContentTitle("New message")
				.setContentText("You received a message from " + user.getUsername());
		mBuilder.setContentIntent(resultPendingIntent);
		
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(1, mBuilder.build());
		return resultIntent;
	}
}
