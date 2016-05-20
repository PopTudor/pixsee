package com.marked.pixsee.friends.friends;

import android.view.View;

import com.marked.pixsee.commands.Command;
import com.marked.pixsee.data.User;
import com.marked.pixsee.friends.commands.FabCommand;
import com.marked.pixsee.friends.commands.OpenCameraCommand;
import com.marked.pixsee.friends.data.FriendsDatasource;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor Pop on 23-Mar-16.
 */
public class FriendPresenter implements FriendsContract.Presenter {
	private FriendsDatasource repository;
	private FriendsContract.View mView;

	@Inject
	public FriendPresenter(FriendsContract.View view, FriendsDatasource repository) {
		this.repository = repository;
		this.mView = view;
		this.mView.setPresenter(this);
	}

	@Inject
	OpenCameraCommand openCamera;
	@Inject
	FabCommand fabCommand;

	@Override
	public void loadMore(String text, int limit) {
		repository.getUsers()
				.debounce(300, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<User>>() {
					@Override
					public void call(List<User> users) {
						mView.onFriendsReplace(users);
					}
				});
	}

	int size = 0;

	public int getSize() {
		return size;
	}

	@Override
	public void loadMore(boolean forceUpdate, final int limit) {
//		repository.add(new User("123", "Pop Tudor", "tudor08pop@yahoo.com", "dea"));
//		repository.add(new User("124", "Sima Ioana", "skumpic_ioana@yahoo.com", "asd"));
//		repository.add(new User("125", "Popa Cristian", "cristipopa@ymail.com", "dsa"));
//		repository.add(new User("126", "Marcel Mirel", "cristipopa@ymail.com", "zxc"));
//		repository.add(new User("127", "Milica Ionut", "cristipopa@ymail.com", "cxz"));
//		repository.add(new User("128", "Andrei Bogdan", "cristipopa@ymail.com", "xcv"));
		if (forceUpdate) {
			final Subscription subscription = repository.getUsers()
													  .observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Action1<List<User>>() {
						           @Override
						           public void call(List<User> users) {
							           if (users.size() > 0) {
								           mView.setRecyclerViewVisibility(View.VISIBLE);
								           mView.onFriendsInsert(users, 0, users.size());
								           size = users.size();
							           }
						           }
					           }
							, new Action1<Throwable>() {
								@Override
								public void call(Throwable throwable) {
									mView.showNoFriends();
								}
							});
		} else {
			repository.getUsers()
					//                    .flatMap { Observable.from(it) }
					//                    .skip(size)
					//                    .take(limit) /* take unsubscribes automatically */
					//                    .toList()
					.subscribe(new Action1<List<User>>() {
						           @Override
						           public void call(List<User> users) {
							           mView.onFriendsInsert(users, size, limit);
							           size += users.size();
						           }
					           }
					);
		}
	}

	@Override
	public void start() {

	}

	@Override
	public void execute(Command command) {
		command.execute();
	}

	@Override
	public List<User> getFriends() {
		return null;
	}
	@Override
	public void loadMore(@NotNull int num) {
		loadMore(false, num);
	}

	@Override
	public void clear() {

	}
}
