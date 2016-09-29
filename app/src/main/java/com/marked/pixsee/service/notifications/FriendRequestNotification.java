package com.marked.pixsee.service.notifications;

import android.app.Notification;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.marked.pixsee.R;
import com.marked.pixsee.main.MainActivity;
import com.marked.pixsee.model.user.User;

/**
 * Created by Tudor on 22-Jul-16.
 */
class FriendRequestNotification extends FcmNotification<User> {
    FriendRequestNotification(Context context, User notificationObject) {
        super(context, notificationObject);
    }

	@Override
    Intent createIntent() {
        Intent intent = new Intent(mContext, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.setAction(mContext.getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			intent.addCategory(Notification.CATEGORY_SOCIAL);
		}
        intent.putExtra(mContext.getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION), notificationObject);
        return intent;
    }

    @Override
    Notification.Builder createNotificationBuilder() {
        Notification.Builder mBuilder = new Notification
                .Builder(mContext)
				.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE |
                        Notification.DEFAULT_LIGHTS | Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
				.setSmallIcon(R.drawable.pixsee_v2)
				.setPriority(Notification.PRIORITY_DEFAULT)
				.setContentTitle("Friend Request")
                .setContentText("You received a friend request from " + notificationObject.getUsername());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mBuilder.setCategory(Notification.CATEGORY_SOCIAL);
		}
        return mBuilder;
    }

    @Override
    TaskStackBuilder createTaskStackBuilder() {
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        taskStackBuilder.addParentStack(MainActivity.class);
        return taskStackBuilder;
    }
}
