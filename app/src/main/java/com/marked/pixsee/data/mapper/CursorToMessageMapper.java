package com.marked.pixsee.data.mapper;

import android.database.Cursor;

import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.data.database.DatabaseContract;

/**
 * Created by Tudor on 2016-05-19.
 */
public class CursorToMessageMapper implements Mapper<Cursor,Message> {
	private final int bodyText;
	private final int type;
	private final int delay;
	private final int date;
	private final int to;
	private final int id;

	public CursorToMessageMapper(Cursor cursor){
		bodyText = cursor.getColumnIndex(DatabaseContract.Message.COLUMN_DATA_BODY);
		type = cursor.getColumnIndex(DatabaseContract.Message.COLUMN_TYPE);
		delay = cursor.getColumnIndex(DatabaseContract.Message.COLUMN_DELAY_WITH_IDLE);
		date = cursor.getColumnIndex(DatabaseContract.Message.COLUMN_DATE);
		to = cursor.getColumnIndex(DatabaseContract.Message.COLUMN_TO);
		id = cursor.getColumnIndex(DatabaseContract.Message.COLUMN_ID);
	}

	@Override
	public Message map(Cursor cursor) {
		return new Message.Builder()
				.messageType(cursor.getInt(type))
				.to(cursor.getString(to))
				.addData(DatabaseContract.Message.COLUMN_DATA_BODY,cursor.getString(bodyText))
				.date(cursor.getString(date))
				.id(cursor.getString(id))
				.build();
	}
}
