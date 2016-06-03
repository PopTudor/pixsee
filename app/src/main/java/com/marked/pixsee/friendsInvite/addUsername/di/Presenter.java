package com.marked.pixsee.friendsInvite.addUsername.di;

import android.util.Log;

import com.marked.pixsee.friends.data.User;
import com.marked.pixsee.friendsInvite.addUsername.AddUsernameContract;
import com.marked.pixsee.friendsInvite.addUsername.data.Repository;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Tudor on 03-Jun-16.
 */
class Presenter implements AddUsernameContract.Presenter {
	private WeakReference<AddUsernameContract.View> mView;
	private Repository repository;

	public Presenter(AddUsernameContract.View view, Repository repository) {
		mView = new WeakReference<>(view);
		mView.get().setPresenter(this);
		this.repository = repository;
	}

	@Override
	public void start() {

	}

	@Override
	public void search(@NotNull String usernameOrEmail) {
		if (usernameOrEmail.isEmpty())
			return;
		repository.getUsers(usernameOrEmail)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<User>>() {
					@Override
					public void call(List<User> users) {
						mView.get().showUsers(users);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						Log.d("***", "call: "+throwable);
					}
				});

	}

	@Override
	public void onClick(User user, int position) {

	}
}
