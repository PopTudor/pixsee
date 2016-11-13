package com.marked.pixsee.ui.friends.mapper;

import android.content.ContentValues;

import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.friends.FriendConstants;
import com.marked.pixsee.data.user.User;

/**
 * Created by Tudor Pop on 29-Mar-16.
 */
public class UserToCvMapper implements Mapper<User,ContentValues> {
	@Override
	public ContentValues map(User user) {
		ContentValues values = new ContentValues();
		values.put(FriendConstants.ID, user.getId());
		values.put(FriendConstants.NAME, user.getName());
		values.put(FriendConstants.EMAIL, user.getEmail());
		values.put(FriendConstants.TOKEN, user.getPushToken());
		values.put(FriendConstants.USERNAME, user.getUsername());
		values.put(FriendConstants.PASSWORD, user.getPassword());
		return values;
	}
}
