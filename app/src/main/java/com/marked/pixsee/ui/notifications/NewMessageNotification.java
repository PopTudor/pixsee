package com.marked.pixsee.ui.notifications;

import android.app.Notification;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.gson.Gson;
import com.marked.pixsee.Pixsee;
import com.marked.pixsee.R;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.injection.Injectable;
import com.marked.pixsee.injection.components.SessionComponent;
import com.marked.pixsee.ui.chat.ChatActivity;
import com.marked.pixsee.ui.chat.data.ChatRepository;
import com.marked.pixsee.ui.chat.data.MessageConstants;

import javax.inject.Inject;

/**
 * Created by Tudor on 22-Jul-16.
 */
class NewMessageNotification extends FcmNotification<Message> implements Injectable {
	@Inject
	Gson mGson;
	@Inject
	ChatRepository mChatRepository;

    NewMessageNotification(Context context, Message notificationObject) {
        super(context, notificationObject);
	    injectComponent();
	    mChatRepository.saveMessage(notificationObject);
    }

    @Override
    Intent createIntent() {
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.setAction(mContext.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION));
	    intent.putExtra(ChatActivity.EXTRA_CONTACT, getUserFromMessageData());
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		    intent.addCategory(Notification.CATEGORY_MESSAGE);
        }

        return intent;
    }

	private User getUserFromMessageData() {
		User user = mGson.fromJson(notificationObject.getData().get("user"), User.class);
		if (user == null)
			throw new NullPointerException("Json message needs a user property! eg {user:...}");
		return user;
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

	@Override
	public void injectComponent() {
		SessionComponent sessionComponent = ((Pixsee) mContext.getApplicationContext()).getSessionComponent();
		DaggerNotificationComponent.builder().sessionComponent(sessionComponent).build().inject(this);
	}
}
