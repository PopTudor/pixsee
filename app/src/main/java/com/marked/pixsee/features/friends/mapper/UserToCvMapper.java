package com.marked.pixsee.features.friends.mapper;

import android.content.ContentValues;

import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.features.friends.data.FriendConstants;

/**
 * Created by Tudor Pop on 29-Mar-16.
 */
public class UserToCvMapper implements Mapper<User,ContentValues> {
	@Override
	public ContentValues map(User user) {
		ContentValues values = new ContentValues();
		values.put(FriendConstants.ID, user.getUserID());
		values.put(FriendConstants.NAME, user.getName());
		values.put(FriendConstants.EMAIL, user.getEmail());
		values.put(FriendConstants.TOKEN, user.getToken());
		values.put(FriendConstants.USERNAME, user.getUsername());
		values.put(FriendConstants.COVER_URL, user.getCoverUrl());
		values.put(FriendConstants.PASSWORD, user.getPassword());
		values.put(FriendConstants.ICON_URL, user.getIconUrl());
		return values;
	}
}
