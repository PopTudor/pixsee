package com.marked.pixsee.ui.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Tudor on 01-Aug-16.
 * Base class for creating new types of notifications
 * Implamentation of Template Method Pattern
 */
abstract class FcmNotification<T> {
    final Context mContext;
    final T notificationObject;

    FcmNotification(Context context, T notificationObject) {
        this.mContext = context;
        this.notificationObject = notificationObject;
    }

	final Notification buildNotification() {
		Intent intent = createIntent();

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = createTaskStackBuilder();
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = createNotificationBuilder();
        builder.setContentIntent(pendingIntent);

        return builder.build();
    }

    abstract Intent createIntent();

    abstract Notification.Builder createNotificationBuilder();

    /*https://developer.android.com/guide/topics/ui/notifiers/notifications.html#NotificationResponse*/
    abstract TaskStackBuilder createTaskStackBuilder();

}
