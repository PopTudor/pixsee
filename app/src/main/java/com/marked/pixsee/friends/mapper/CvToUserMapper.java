package com.marked.pixsee.friends.mapper;

import android.content.ContentValues;

import com.marked.pixsee.data.mapper.Mapper;
import com.marked.pixsee.friends.data.FriendContractDB;
import com.marked.pixsee.friends.data.User;

/**
 * Created by Tudor Pop on 29-Mar-16.
 */
public class CvToUserMapper implements Mapper<ContentValues, User> {
	@Override
	public User map(ContentValues values) {
		String id = values.getAsString(FriendContractDB.COLUMN_ID);
		String name = values.getAsString(FriendContractDB.COLUMN_NAME);
		String email = values.getAsString(FriendContractDB.COLUMN_EMAIL);
		String token = values.getAsString(FriendContractDB.COLUMN_TOKEN) ;
		return new User(id, name, email, token);
	}
}
