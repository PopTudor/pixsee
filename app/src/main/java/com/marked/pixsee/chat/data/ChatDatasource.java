package com.marked.pixsee.chat.data;

import android.support.annotation.NonNull;

import com.marked.pixsee.model.user.User;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 2016-05-19.
 * Main entry point for accessing Message data.
 * <p/>
 */
public interface ChatDatasource {
	Observable<List<Message>> getMessages(User friend);

	Observable<Message> getMessage(@NonNull Message messageId);

	void saveMessage(@NonNull Message message);

	void updateMessage(@NonNull Message message);

	void saveMessage(@NonNull List<Message> message);

	void refreshMessages();

	void deleteAllMessages();

	void deleteMessages(@NonNull Message messageId);
}
