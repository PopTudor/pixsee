package com.marked.pixsee.service.notifications.mapper;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.features.chat.data.MessageConstants;

/**
 * Created by Tudor on 01-Aug-16.
 */
public class RemoteMessageToMessageMapper implements Mapper<RemoteMessage, Message> {
	@Override
	public Message map(RemoteMessage remoteMessage) {
		String to = getNotNullString(remoteMessage.getData().get(MessageConstants.TO));
		String from = getNotNullString(remoteMessage.getData().get(MessageConstants.FROM));

		return new Message.Builder()
				.addData(remoteMessage.getData())
				       .to(to)
				       .from(from)
				.messageType(getMessageType(remoteMessage))
				.date(String.valueOf(remoteMessage.getSentTime()))
				.build();
	}

	private String getNotNullString(String s) {
		if (s == null)
			throw new NullPointerException();
		return s;
	}

	private int getMessageType(RemoteMessage remoteMessage){
        String messageType = remoteMessage.getData().get(MessageConstants.MESSAGE_TYPE);
        if (messageType == null)
            return 0;
        return Integer.parseInt(messageType);
    }
}
