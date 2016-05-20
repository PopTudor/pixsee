package com.marked.pixsee.friends.cards.data;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 2016-05-19.
 * Main entry point for accessing Message data.
 * <p/>
 */
public interface CardDatasource {
	Observable<List<Message>> getMessages();

	Observable<Message> getMessage(@NonNull Message messageId);

	void saveMessage(@NonNull Message message);

	void updateMessage(@NonNull Message message);

	void saveMessage(@NonNull List<Message> message);

	void refreshMessages();

	void deleteAllMessages();

	void deleteMessages(@NonNull Message messageId);
}
