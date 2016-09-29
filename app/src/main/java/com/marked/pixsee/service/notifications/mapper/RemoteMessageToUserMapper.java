package com.marked.pixsee.service.notifications.mapper;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.friends.data.FriendConstants;
import com.marked.pixsee.model.Mapper;
import com.marked.pixsee.model.user.User;

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
				message.getData().get(FriendConstants.COVER_URL),
				message.getData().get(FriendConstants.ICON_URL),
				message.getData().get(FriendConstants.USERNAME));
	}
}
