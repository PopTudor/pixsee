package com.marked.pixsee.ui.notifications.mapper;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.ui.friends.data.FriendConstants;

/**
 * Created by Tudor on 21-Jul-16.
 */
public class RemoteMessageToUserMapper implements Mapper<RemoteMessage,User> {
	@Override
	public User map(RemoteMessage message) {
		return new User(message.getData().get(FriendConstants.ID),
				message.getData().get(FriendConstants.NAME),
				message.getData().get(FriendConstants.EMAIL),
				message.getData().get(FriendConstants.TOKEN), null,
				message.getData().get(FriendConstants.USERNAME));
	}
}
