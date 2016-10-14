package com.marked.pixsee.features.friends.mapper;

import android.database.Cursor;

import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.features.friends.data.FriendContractDB;

/**
 * Created by Tudor Pop on 29-Mar-16.
 */
public class CursorToUserMapper implements Mapper<Cursor, User> {
	private int id;
	private int name;
	private int email;
	private int coverUrl;
	private int iconUrl;
	private int username;
	private int token;
	private boolean hasIndexses = false;

	@Override
	public User map(Cursor cursor) {
		if (!hasIndexses)
			readColumnIndex(cursor);
		return new User(cursor.getString(id), cursor.getString(name), cursor.getString(email), cursor.getString(token),null,
				cursor.getString(coverUrl),cursor.getString(iconUrl),cursor.getString(username));
	}

	/**
	 * Before calling map, you have to call this methods to find out the column index for each property
	 * @param cursor the cursor
	 */
	private void readColumnIndex(Cursor cursor){
		id = cursor.getColumnIndex(FriendContractDB.COLUMN_ID);
		name = cursor.getColumnIndex(FriendContractDB.COLUMN_NAME);
		email = cursor.getColumnIndex(FriendContractDB.COLUMN_EMAIL);
		token = cursor.getColumnIndex(FriendContractDB.COLUMN_TOKEN);
		coverUrl = cursor.getColumnIndex(FriendContractDB.COLUMN_COVER_URL);
		iconUrl = cursor.getColumnIndex(FriendContractDB.COLUMN_ICON_URL);
		username = cursor.getColumnIndex(FriendContractDB.COLUMN_USERNAME);
		hasIndexses = true;
	}
}
