package com.marked.pixsee.features.chat.custom;

import com.marked.pixsee.features.chat.data.Message;

/**
 * Created by Tudor on 18-Jul-16.
 */
public interface Bindable  {
	void bindMessage(final Message message, final ChatAdapter.ChatInteraction chatInteraction);
}
