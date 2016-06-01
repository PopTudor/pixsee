package com.marked.pixsee.friends;

import android.view.View;

import com.marked.pixsee.commands.Command;
import com.marked.pixsee.friends.commands.FabCommand;
import com.marked.pixsee.friends.data.User;
import com.marked.pixsee.friends.data.datasource.FriendsDatasource;
import com.marked.pixsee.main.commands.SelfieCommand;

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
	SelfieCommand openCamera;
	@Inject
	FabCommand fabCommand;

	@Override
	public void loadMore(int limit, String text) {
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
	public void loadMore(final int limit, boolean forceUpdate) {
		if (forceUpdate) {
			final Subscription subscription = repository.refreshUsers()
													  .observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Action1<List<User>>() {
						           @Override
						           public void call(List<User> users) {
							           if (users.size() > 0) {
								           mView.setRecyclerViewVisibility(View.VISIBLE);
								           mView.onFriendsReplace(users);
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
			final Subscription subscription = repository.getUsers()
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Action1<List<User>>() {
								   @Override
								   public void call(List<User> users) {
									   if (users.size() > 0) {
										   mView.setRecyclerViewVisibility(View.VISIBLE);
										   mView.onFriendsReplace(users);
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
		}
	}

	@Override
	public void start() {
		loadMore(25, false);
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
	public void loadMore(int num) {
		loadMore(num, false);
	}

	@Override
	public void clear() {

	}
}
