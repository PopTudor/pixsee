package com.marked.pixsee.data.chat;

/**
 * Created by Tudor on 25-Nov-16.
 */

public class TypingMessage {
	private String from;
	private String fromToken;
	private String to;
	private String toToken;
	private boolean typing;

	public TypingMessage(String from, String fromToken, String to, String toToken, boolean typing) {
		this.setFrom(from);
		this.setFromToken(fromToken);
		this.setTo(to);
		this.setToToken(toToken);
		this.setTyping(typing);
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFromToken() {
		return fromToken;
	}

	public void setFromToken(String fromToken) {
		this.fromToken = fromToken;
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

	public boolean isTyping() {
		return typing;
	}

	public void setTyping(boolean typing) {
		this.typing = typing;
	}
}
