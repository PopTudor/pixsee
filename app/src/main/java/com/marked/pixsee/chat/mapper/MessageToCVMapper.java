package com.marked.pixsee.chat.mapper;

import android.content.ContentValues;

import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.model.Mapper;

import java.util.Map;

/**
 * Created by Tudor on 2016-05-19.
 */
public class MessageToCVMapper implements Mapper<Message, ContentValues> {
	@Override
	public ContentValues map(Message message) {
		Map<String,String> data = message.getData();
		String to = message.getTo();
		String id = message.getId();
		Integer messageType = message.getMessageType();
		String date = message.getDate();

		ContentValues values = new ContentValues();
		if (data.containsKey(MessageConstants.DATA_BODY))
			values.put(MessageConstants.DATA_BODY, data.get(MessageConstants.DATA_BODY));
		if (to != null && !to.isEmpty())
			values.put("_" + MessageConstants.TO, to);
		if (id != null && !id.isEmpty())
			values.put(MessageConstants.ID, id);
		//		if (!source.isNullOrBlank())
		//			values.put(MessageConstants.SOURCE, source)
		values.put(MessageConstants.MESSAGE_TYPE, messageType);
		values.put(MessageConstants.CREATION_DATE, date);

		return values;
	}
}
