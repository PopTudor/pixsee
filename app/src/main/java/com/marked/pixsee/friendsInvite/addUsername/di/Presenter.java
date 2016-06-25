package com.marked.pixsee.friendsInvite.addUsername.di;

import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.data.repository.user.UserDatasource;
import com.marked.pixsee.friendsInvite.addUsername.AddUsernameContract;
import com.marked.pixsee.friendsInvite.addUsername.data.RequestService;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Tudor on 03-Jun-16.
 */
class Presenter implements AddUsernameContract.Presenter {
	private WeakReference<AddUsernameContract.View> mView;
	private UserDatasource repository;
	private RequestService requestService;


	public Presenter(AddUsernameContract.View view, UserDatasource repository, RequestService requestService) {
		this.requestService = requestService;
		mView = new WeakReference<>(view);
		mView.get().setPresenter(this);
		this.repository = repository;
	}

	@Override
	public void attach() {

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
						if (throwable instanceof SocketTimeoutException)
							mView.get().showNoInternetConnection();
					}
				});

	}

	@Override
	public void onClick(User user, int position) {
		requestService.friendRequest(user);
	}
}
