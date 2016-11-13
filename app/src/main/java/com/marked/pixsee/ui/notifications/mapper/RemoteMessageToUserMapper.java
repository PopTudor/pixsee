package com.marked.pixsee.ui.notifications.mapper;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.user.User;

/**
 * Created by Tudor on 21-Jul-16.
 */
public class RemoteMessageToUserMapper implements Mapper<RemoteMessage, User> {
	private Gson mGson = new Gson();

	@Override
	public User map(RemoteMessage message) {
		return mGson.fromJson(message.getData().get("user"), User.class);
	}
}
