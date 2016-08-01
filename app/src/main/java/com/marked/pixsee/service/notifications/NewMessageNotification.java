package com.marked.pixsee.service.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.R;
import com.marked.pixsee.chat.ChatActivity;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.data.MessageConstants;
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
		Message message = (Message) mMapper.map(mRemoteMessage);

		Intent intent = new Intent(mContext, ChatActivity.class);
		intent.setAction(mContext.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION));
		intent.putExtra(mContext.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION), message);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			intent.addCategory(Notification.CATEGORY_MESSAGE);
		}

		// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ChatActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(intent);

		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification.Builder builder = new Notification.Builder(mContext)
				.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE |
						Notification.DEFAULT_LIGHTS | Notification.PRIORITY_HIGH)
				.setAutoCancel(true)
				.setSmallIcon(R.drawable.ic_chat_24dp)
				.setPriority(Notification.PRIORITY_HIGH)
				.setContentTitle("New message")
				.setContentText(message.getData().get(MessageConstants.DATA_BODY))
				.setContentIntent(pendingIntent);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			builder.setCategory(Notification.CATEGORY_MESSAGE);
		}

		return builder.build();
	}
}
