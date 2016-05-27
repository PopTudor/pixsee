package com.marked.pixsee.data.mapper;

import android.content.ContentValues;

import com.marked.pixsee.friends.data.User;
import com.marked.pixsee.data.database.DatabaseContract;

/**
 * Created by Tudor Pop on 29-Mar-16.
 */
public class CvToUserMapper implements Mapper<ContentValues, User> {
	@Override
	public User map(ContentValues values) {
		String id = values.getAsString(DatabaseContract.Friend.COLUMN_ID);
		String name = values.getAsString(DatabaseContract.Friend.COLUMN_NAME);
		String email = values.getAsString(DatabaseContract.Friend.COLUMN_EMAIL);
		String token = values.getAsString(DatabaseContract.Friend.COLUMN_TOKEN) ;
		return new User(id, name, email, token);
	}
}
