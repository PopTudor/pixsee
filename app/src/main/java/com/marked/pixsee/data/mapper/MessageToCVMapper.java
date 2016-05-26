package com.marked.pixsee.data.mapper;

import android.content.ContentValues;

import com.marked.pixsee.friends.cards.data.Message;
import com.marked.pixsee.friends.cards.data.MessageConstants;

import java.util.Map;

/**
 * Created by Tudor on 2016-05-19.
 */
public class MessageToCVMapper implements Mapper<Message, ContentValues> {
	@Override
	public ContentValues map(Message message) {
		String collapseKey = message.getCollapseKey();
		Integer timeToLive = message.getTimeToLive();
		Boolean isDelayWhileIdle = message.getDelayWhileIdle();
		String restrictedPackageName = message.getRestrictedPackageName();
		Map<String,String> data = message.getData();
		String to = message.getTo();
		String id = message.getId();
		Integer messageType = message.getMessageType();
		Long date = message.getDate();

		ContentValues values = new ContentValues();
		if (collapseKey != null && !collapseKey.isEmpty())
			values.put(MessageConstants.COLLAPSE_OPTION, collapseKey);
		if (timeToLive != null)
			values.put(MessageConstants.TIME_TO_LIVE_OPTION, timeToLive);
		if (isDelayWhileIdle!=null)
			values.put(MessageConstants.DELAY_WHILE_IDLE_OPTION, isDelayWhileIdle);
		if (restrictedPackageName != null && !restrictedPackageName.isEmpty())
			values.put(MessageConstants.RESTRICTED_PACKAGE_NAME_OPTION, restrictedPackageName);
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
