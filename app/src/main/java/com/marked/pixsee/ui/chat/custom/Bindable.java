package com.marked.pixsee.ui.chat.custom;

import com.marked.pixsee.data.message.Message;

/**
 * Created by Tudor on 18-Jul-16.
 */
interface Bindable {
	void bindMessage(final Message message, final ChatAdapter.ChatInteraction chatInteraction);
}
