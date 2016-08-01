package com.marked.pixsee.service.notifications.mapper;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.data.Mapper;

/**
 * Created by Tudor on 01-Aug-16.
 */
public class RemoteMessageToMessageMapper implements Mapper<RemoteMessage, Message> {
	@Override
	public Message map(RemoteMessage remoteMessage) {
		return new Message.Builder()
				.addData(remoteMessage.getData())
				.to(remoteMessage.getData().get(MessageConstants.TO))
				.from(remoteMessage.getData().get(MessageConstants.FROM))
				.messageType(Integer.parseInt(remoteMessage.getData().get(MessageConstants.MESSAGE_TYPE)))
				.date(String.valueOf(remoteMessage.getSentTime()))
				.build();
	}
}
