package com.marked.pixsee.data.mapper;

import android.database.Cursor;

import com.marked.pixsee.friends.data.DatabaseFriendContract;
import com.marked.pixsee.friends.data.User;

/**
 * Created by Tudor Pop on 29-Mar-16.
 */
public class CursorToUserMapper implements Mapper<Cursor, User> {
	private int id;
	private int name;
	private int email;
	private int token;
	private boolean hasIndexses = false;

	@Override
	public User map(Cursor cursor) {
		if (!hasIndexses)
			readColumnIndex(cursor);
		return new User(cursor.getString(id), cursor.getString(name), cursor.getString(email), cursor.getString(token));
	}

	/**
	 * Before calling map, you have to call this methods to find out the column index for each property
	 * @param cursor the cursor
	 */
	private void readColumnIndex(Cursor cursor){
		id = cursor.getColumnIndex(DatabaseFriendContract.COLUMN_ID);
		name = cursor.getColumnIndex(DatabaseFriendContract.COLUMN_NAME);
		email = cursor.getColumnIndex(DatabaseFriendContract.COLUMN_EMAIL);
		token = cursor.getColumnIndex(DatabaseFriendContract.COLUMN_TOKEN);
		hasIndexses = true;
	}
}
