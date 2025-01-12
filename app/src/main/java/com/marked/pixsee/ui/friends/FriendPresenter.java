package com.marked.pixsee.ui.friends;

import android.support.annotation.NonNull;
import android.view.View;

import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.ui.commands.Command;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor Pop on 23-Mar-16.
 */
@FragmentScope
class FriendPresenter implements FriendsContract.Presenter {
	private UserDatasource repository;
	private WeakReference<FriendsContract.View> mView;
	private int size = 0;

	FriendPresenter(FriendsContract.View view, UserDatasource repository) {
		this.repository = repository;
		this.mView = new WeakReference<>(view);
	}

	@Override
	public void loadMore(int limit, @NonNull String byName) {
		if (byName.isEmpty())
			return;
		repository.getUsers(byName)
				.debounce(500, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<User>>() {
					@Override
					public void call(List<User> users) {
						mView.get().onFriendsReplace(users);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						throwable.printStackTrace();
					}
				});
	}

	public int getSize() {
		return size;
	}

	@Override
	public void loadMore(final int limit, boolean forceUpdate) {
		if (forceUpdate) {
			repository.refreshUsers()
					.flatMap(new Func1<List<User>, Observable<List<User>>>() {
						@Override
						public Observable<List<User>> call(List<User> users) {
							return Observable.from(users).toSortedList();
						}
					})
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Action1<List<User>>() {
						           @Override
						           public void call(List<User> users) {
							           if (users.size() == 0)
								           mView.get().showNoFriends();
							           mView.get().setRecyclerViewVisibility(View.VISIBLE);
							           mView.get().onFriendsReplace(users);
							           size = users.size();
						           }
					           }
							, new Action1<Throwable>() {
								@Override
								public void call(Throwable throwable) {
									if (throwable instanceof SocketTimeoutException)
										mView.get().showNoInternetConnection();
									else if (throwable instanceof ConnectException)
										mView.get().showNoInternetConnection();
									else
										mView.get().showNoFriends();
									throwable.printStackTrace();
								}
							});
		} else {
			repository.getUsers()
					.sorted()
					.observeOn(AndroidSchedulers.mainThread())
					.filter(new Func1<List<User>, Boolean>() {
						@Override
						public Boolean call(List<User> users) {
							return users != null && users.size() > 0;
						}
					})
					.subscribe(new Action1<List<User>>() {
						           @Override
						           public void call(List<User> users) {
							           mView.get().setRecyclerViewVisibility(View.VISIBLE);
							           mView.get().onFriendsReplace(users);
							           size = users.size();
						           }
					           }
							, new Action1<Throwable>() {
								@Override
								public void call(Throwable throwable) {
									try {
										mView.get().showNoFriends();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
		}
	}

	@Override
	public void attach() {
		loadMore(25, false);
	}


	@Override
	public void execute(Command command) {
		command.execute();
	}

	@Override
	public void loadMore(int num) {
		loadMore(num, false);
	}

	@Override
	public void clear() {
		repository.clear();
	}

	@Override
	public void actionInviteClick() {
		mView.get().showInviteFriends();
	}

	@Override
	public void detach() {

	}
}
