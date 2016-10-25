package com.marked.pixsee.data.chat;

import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.ui.chat.data.MessageConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Tudor on 19-Jun-16.
 */
public class MessageTestUtils {
	public static int sMessageTypes[] = {
			MessageConstants.MessageType.ME_IMAGE, MessageConstants.MessageType.ME_MESSAGE,
			MessageConstants.MessageType.YOU_IMAGE, MessageConstants.MessageType.YOU_MESSAGE
	};
	public static String sTo[] = {
			"z", "x", "c", "v", "b"
	};
	static Random mRandom = new Random();

	public static Message getRandomMessage() {
		int randomMessageType = sMessageTypes[mRandom.nextInt(sMessageTypes.length)];
		String randomTo = sTo[mRandom.nextInt(sTo.length)];
		return new Message.Builder()
				       .messageType(randomMessageType)
				       .from("ABC")
				       .to(randomTo)
				       .id(UUID.randomUUID().toString())
				       .addData(MessageConstants.DATA_BODY, "test message text")
				       .build();
	}

	public static List<Message> getRandomMessageList(int number) {
		List<Message> messages = new ArrayList<>();
		for (int i = 0; i < number; i++)
			messages.add(getRandomMessage());
		return messages;
	}
}
