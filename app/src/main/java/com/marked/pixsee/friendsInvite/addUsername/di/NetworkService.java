package com.marked.pixsee.friendsInvite.addUsername.di;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.friends.data.FriendConstants;

import java.util.UUID;

/**
 * Created by Tudor on 04-Jun-16.
 */
class NetworkService implements com.marked.pixsee.friendsInvite.addUsername.data.RequestService {
	private final String senderID;
	private final User thisUser;
	final FirebaseMessaging fm = FirebaseMessaging.getInstance();

	public NetworkService(User thisUser, String senderID) {
		this.thisUser = thisUser;
		this.senderID = senderID;
	}

	/**
	 * Send a friend request to the server
	 * @param user the user that gets the request
	 */
	@Override
	public void friendRequest(User user) {
		final String msgId = UUID.randomUUID().toString();
		final String token = user.getToken();

		RemoteMessage remoteMessage = new RemoteMessage.Builder(senderID + "@gcm.googleapis.com")
				.setMessageId(msgId)
				.setMessageType("friend_request")
				.addData(FriendConstants.ID,thisUser.getUserID())
				.addData(FriendConstants.USERNAME,thisUser.getUsername())
				.addData(FriendConstants.ICON_URL,thisUser.getIconUrl())
				.addData(FriendConstants.NAME,thisUser.getName())
				.addData(FriendConstants.EMAIL,thisUser.getEmail())
				.addData(Message.TO,token)
				.build();
		fm.send(remoteMessage);
	}
}
