package com.marked.pixsee.service.notifications;

import android.app.Notification;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.marked.pixsee.R;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.features.chat.ChatActivity;
import com.marked.pixsee.features.chat.data.MessageConstants;

/**
 * Created by Tudor on 22-Jul-16.
 */
class NewMessageNotification extends FcmNotification<Message> {
    NewMessageNotification(Context context, Message notificationObject) {
        super(context, notificationObject);
    }

    @Override
    Intent createIntent() {
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.setAction(mContext.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION));
        intent.putExtra(mContext.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION), notificationObject);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addCategory(Notification.CATEGORY_MESSAGE);
        }
        return intent;
    }

    @Override
    Notification.Builder createNotificationBuilder() {
        Notification.Builder builder = new Notification.Builder(mContext)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE |
                        Notification.DEFAULT_LIGHTS | Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_chat_24dp)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle("New message")
                .setContentText(notificationObject.getData().get(MessageConstants.DATA_BODY));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE);
        }
        return builder;
    }

    @Override
    TaskStackBuilder createTaskStackBuilder() {
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        taskStackBuilder.addParentStack(ChatActivity.class);

        return taskStackBuilder;
    }
}
