package com.marked.pixsee.data.mapper;

import android.database.Cursor;

import com.marked.pixsee.friends.cards.data.Message;

/**
 * Created by Tudor on 2016-05-19.
 */
public class CursorToMessageMapper implements Mapper<Cursor,Message> {
	@Override
	public Message map(Cursor cursor) {
//		String id = values.getAsString(DatabaseContract.Friend.COLUMN_ID);
//		String name = values.getAsString(DatabaseContract.Friend.COLUMN_NAME);
//		String email = values.getAsString(DatabaseContract.Friend.COLUMN_EMAIL);
//		String token = values.getAsString(DatabaseContract.Friend.COLUMN_TOKEN) ;
//		return new User(id, name, email, token);
		return null;
	}
}
