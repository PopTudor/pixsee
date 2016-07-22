package com.marked.pixsee.entry;

import android.content.Intent;

import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.friends.data.FriendConstants;

/**
 * Created by Tudor on 21-Jul-16.
 */
public class IntentToUserMapper implements Mapper<Intent, User> {
	@Override
	public User map(Intent intent) {
		return new User(
				intent.getStringExtra(FriendConstants.ID),
				intent.getStringExtra(FriendConstants.NAME),
				intent.getStringExtra(FriendConstants.EMAIL),
				intent.getStringExtra(MessageConstants.FROM), null,
				intent.getStringExtra(FriendConstants.COVER_URL),
				intent.getStringExtra(FriendConstants.ICON_URL),
				intent.getStringExtra(FriendConstants.USERNAME)
		);
	}
}
