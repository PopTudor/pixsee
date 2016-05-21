package com.marked.pixsee.friends.cards.data;

import android.support.annotation.NonNull;


import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Tudor on 2016-05-19.
 */
public class CardRepository implements CardDatasource {
	private CardDatasource disk;
	private CardDatasource network;
	private List<Message> cache = new ArrayList<>();
	private boolean dirtyCache;

	public CardRepository(CardDatasource disk, CardDatasource network) {
		this.disk = disk;
		this.network = network;
	}

	@Override
	public Observable<List<Message>> getMessagesOfFriend(String friendId) {
		if (cache.size() != 0 && !dirtyCache)
			return Observable.just(cache);
		
		// Query the local storage if available. If not, query the network.
		disk.getMessagesOfFriend(friendId).doOnNext(new Action1<List<Message>>() {
			@Override
			public void call(List<Message> message) {
				cache.clear();
				cache.addAll(message);
			}
		}).subscribe();
		
		if (cache.size() != 0)
			return Observable.just(cache);
		else
			return network.getMessagesOfFriend(friendId)
					       .flatMap(new Func1<List<Message>, Observable<Message>>() {
						       @Override
						       public Observable<Message> call(List<Message> messagess) {
							       return Observable.from(messagess);
						       }
					       })
					       .doOnNext(new Action1<Message>() {
						       @Override
						       public void call(Message messages) {
							       disk.saveMessage(messages);
							       cache.clear();
							       cache.add(messages);
						       }
					       })
					       .doOnCompleted(new Action0() {
						       @Override
						       public void call() {
							       dirtyCache = false;
						       }
					       }).toList();
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
