package com.marked.pixsee.friends.cards.data;

import android.support.annotation.NonNull;

import com.marked.pixsee.data.message.Message;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 2016-05-20.
 */
public class MessageRemoteDatasource implements MessageDatasource {
	@Override
	public Observable<List<Message>> getMessages() {
		return null;
	}

	@Override
	public Observable<Message> getMessage(@NonNull Message MessageId) {
		return null;
	}

	@Override
	public void saveMessage(@NonNull Message Message) {

	}

	@Override
	public void updateMessage(@NonNull Message message) {

	}

	@Override
	public void saveMessage(@NonNull List<Message> Message) {

	}

	@Override
	public void refreshMessages() {

	}

	@Override
	public void deleteAllMessages() {

	}

	@Override
	public void deleteMessages(@NonNull Message MessageId) {

	}
}
