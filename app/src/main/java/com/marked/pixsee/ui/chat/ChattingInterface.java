package com.marked.pixsee.ui.chat;

/**
 * Created by Tudor on 13-Oct-16.
 */

interface ChattingInterface {
	void connect();

	void emit(String event, Object... objects);

	void disconnect();
}
