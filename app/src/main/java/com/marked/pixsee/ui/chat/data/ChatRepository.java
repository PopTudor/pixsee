package com.marked.pixsee.ui.chat.data;

import android.support.annotation.NonNull;

import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.data.user.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 2016-05-19.
 *
 * https://frogermcs.github.io/dependency-injection-with-dagger-2-the-api/
 * All parameters are taken from dependencies graph. @Inject annotation used in costructor
 * also makes this class a part of dependencies graph.
 * It means that it can also be injected when it’s needed:
 * @Inject ChatNetworkDatasource networkData;
 * and it can also be provided to methods that needs this dependency:
 * @provides
 * public Repository provideRepository(ChatNetworkDatasource source,ChatLocalds local){
 *   return new Repository(source,local);
 * }
 * but it cannot be provided for an interface(unless annotated with qualified)
 * the following won't work:
 * @provides
 * public Repository provideRepository(ChatDatasource source){
 *   return new Repository(source);
 * }
 *
 */
public class ChatRepository implements ChatDatasource {
	private final List<Message> cache = new ArrayList<>();
	private ChatDatasource disk;
	private ChatDatasource network;
	private boolean dirtyCache;

	/**
	 * Repository to retrieve data
	 *
	 * @param disk    must be @{link ChatDiskDatasource} or something that implements @{link ChatDatasource}
	 * @param network must be @{link ChatNetworkDatasource} or something that implements @{link ChatDatasource}
	 */
	@Inject
	public ChatRepository(ChatDiskDatasource disk, ChatNetworkDatasource network) {
		this.disk = disk;
		this.network = network;
	}

	/**
	 * Get messages of the requested friend
	 *
	 * @param friendId
	 * @return
	 */
	@Override
	public Observable<List<Message>> getMessages(User friendId) {
		if (cache.size() != 0 && !dirtyCache)
			return Observable.just(cache);
		
		// Query the local storage if available. If not, query the network.
		Observable<List<Message>> local = disk.getMessages(friendId)
				.doOnNext(new Action1<List<Message>>() {
					@Override
					public void call(List<Message> messages) {
						cache.clear();
						cache.addAll(messages);
					}
				});

//		Observable<List<Message>> remote = network.getMessages(friendId)
//					       .flatMap(new Func1<List<Message>, Observable<Message>>() {
//						       @Override
//						       public Observable<Message> call(List<Message> messages) {
//							       disk.saveMessage(messages);
//							       return Observable.from(messages);
//						       }
//					       })
//					       .doOnNext(new Action1<Message>() {
//						       @Override
//						       public void call(Message messages) {
//							       cache.clear();
//							       cache.add(messages);
//						       }
//					       })
//					       .doOnCompleted(new Action0() {
//						       @Override
//						       public void call() {
//							       dirtyCache = false;
//						       }
//					       }).toList();
		/* http://blog.danlew.net/2015/06/22/loading-data-from-multiple-sources-with-rxjava/ */
		return Observable.concat(Observable.just(cache), local)
				.first(new Func1<List<Message>, Boolean>() {
					@Override
					public Boolean call(List<Message> users) {
						return users.size() > 0;
					}
				})
				.subscribeOn(Schedulers.io());
	}

	@Override
	public Observable<Message> getMessage(@NonNull Message MessageId) {
		return Observable.empty();
	}

	@Override
	public void saveMessage(@NonNull Message message) {
		cache.add(message);
		disk.saveMessage(message);
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
	public void deleteMessages(@NonNull Message message) {
		cache.remove(message);
		disk.deleteMessages(message);
	}
}
