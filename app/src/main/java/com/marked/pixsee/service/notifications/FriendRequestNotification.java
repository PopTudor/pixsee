package com.marked.pixsee.service.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.R;
import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.main.MainActivity;

/**
 * Created by Tudor on 22-Jul-16.
 */
class FriendRequestNotification extends FcmNotification {

	public FriendRequestNotification(RemoteMessage remoteMessage, Mapper<RemoteMessage, User> mapper, Context context) {
		super(remoteMessage, mapper, context);
	}

	@Override
	public Notification buildNotification() {
		User user = (User) mMapper.map(mRemoteMessage);

		Intent intent = new Intent(mContext, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.setAction(mContext.getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			intent.addCategory(Notification.CATEGORY_SOCIAL);
		}
		intent.putExtra(mContext.getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION), user);

		// open activity
		PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat
				.Builder(mContext)
				.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE |
							 Notification.DEFAULT_LIGHTS | Notification.PRIORITY_HIGH)
				.setAutoCancel(true)
				.setSmallIcon(R.drawable.pixsee_v2)
				.setContentIntent(resultPendingIntent)
				.setPriority(Notification.PRIORITY_DEFAULT)
				.setContentTitle("Friend Request")
				.setContentText("You received a friend request from " + user.getUsername());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mBuilder.setCategory(Notification.CATEGORY_SOCIAL);
		}
		return mBuilder.build();
	}

}
