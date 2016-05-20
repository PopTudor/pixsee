package com.marked.pixsee.friends.cards.data;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 2016-05-19.
 */
public class CardRepository implements CardDatasource {
	private CardDatasource disk;
	private CardDatasource network;

	public CardRepository(CardDatasource disk, CardDatasource network) {
		this.disk = disk;
		this.network = network;
	}

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
