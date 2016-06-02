package com.marked.pixsee.friends.mapper;

import android.content.ContentValues;

import com.marked.pixsee.data.mapper.Mapper;
import com.marked.pixsee.friends.data.User;
import com.marked.pixsee.friends.data.FriendConstants;

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
		values.put(FriendConstants.ICON_URL, user.getIconUrl());
		return values;
	}
}
