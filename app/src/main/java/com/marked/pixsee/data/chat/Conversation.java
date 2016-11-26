package com.marked.pixsee.data.chat;

import com.marked.pixsee.data.user.User;

/**
 * Created by Tudor on 25-Nov-16.
 */

public class Conversation {
	private User from;
	private String to;
	private String toToken;

	public Conversation(User from, String to, String toToken) {
		this.from = from;
		this.to = to;
		this.toToken = toToken;
	}


	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getToToken() {
		return toToken;
	}

	public void setToToken(String toToken) {
		this.toToken = toToken;
	}
}
