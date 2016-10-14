package com.marked.pixsee.features.chat;

/**
 * Created by Tudor on 13-Oct-16.
 */

interface ChattingInterface {
	void connect();

	void emit(String event, Object... objects);

	void disconnect();
}
