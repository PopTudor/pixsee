package com.marked.pixsee.ui.friends.mapper;

import android.content.ContentValues;

import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.friends.FriendContractDB;
import com.marked.pixsee.data.user.User;

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
		String username = values.getAsString(FriendContractDB.COLUMN_USERNAME) ;

		return new User(id, name, email, token, null, username);
	}
}
