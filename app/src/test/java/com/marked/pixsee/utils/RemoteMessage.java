package com.marked.pixsee.utils;

import com.marked.pixsee.ui.chat.data.MessageConstants;

/**
 * Created by Tudor on 25-Oct-16.
 */

public class RemoteMessage {
	public static com.google.firebase.messaging.RemoteMessage createRemoteMessage(String clickAction) {
		return new com.google.firebase.messaging.RemoteMessage.Builder("abc")
				       .addData(MessageConstants.MESSAGE_TYPE, String.valueOf("2"))
				       .addData(MessageConstants.NOTIFICATION_ACTION_CLICK, clickAction)
				       .addData("from", "xyz")
				       .addData(MessageConstants.FROM, "from")
				       .addData(MessageConstants.TO, "to")
				       .build();
	}
}
