package com.marked.pixsee.ui.friendsInvite.addUsername;

import android.support.annotation.StringDef;

import com.marked.pixsee.data.user.User;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.marked.pixsee.ui.friendsInvite.addUsername.Relationship.Status.FRIEND;
import static com.marked.pixsee.ui.friendsInvite.addUsername.Relationship.Status.RECEIVED_FRIEND_REQUEST;
import static com.marked.pixsee.ui.friendsInvite.addUsername.Relationship.Status.SENT_FRIEND_REQUEST;
import static com.marked.pixsee.ui.friendsInvite.addUsername.Relationship.Status.USER;

/**
 * Created by Tudor on 20-Nov-16.
 */

class Relationship {
	User user;
	String status;

	public Relationship(User user, @Status String status) {
		this.user = user;
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(@Status String status) {
		this.status = status;
	}

	@Retention(RetentionPolicy.SOURCE)
	@StringDef({FRIEND, USER, SENT_FRIEND_REQUEST, RECEIVED_FRIEND_REQUEST})
	@interface Status {
		String FRIEND = "FRIEND";
		String USER = "USER";
		String SENT_FRIEND_REQUEST = "SENT_FRIEND_REQUEST";
		String RECEIVED_FRIEND_REQUEST = "RECEIVED_FRIEND_REQUEST";
	}
}
