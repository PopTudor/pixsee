package com.marked.pixsee.ui.friendsInvite.addUsername;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 03-Jun-16.
 */
class Presenter implements AddUsernameContract.Presenter {
	private final WeakReference<AddUsernameContract.View> mView;
	private final UserDatasource repository;
	private final User mAppsUser;
	private final FriendRequestAPI mFriendRequestAPI;


	public Presenter(AddUsernameContract.View view, UserDatasource repository, User appUser, FriendRequestAPI mFriendRequestAPI) {
		this.mView = new WeakReference<>(view);
		this.mView.get().setPresenter(this);
		this.repository = repository;
		this.mAppsUser = appUser;
		this.mFriendRequestAPI = mFriendRequestAPI;
	}

	@Override
	public void attach() {

	}

	@Override
	public void detach() {

	}

	@Override
	public void search(@NonNull String usernameOrEmail) {
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
		mFriendRequestAPI.friendRequest(mAppsUser, user.getToken())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Response<JsonObject>>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(Response<JsonObject> jsonObjectResponse) {
						// TODO: 22-Jul-16 add a green checkbox if friend request was sent successfully
					}
				});
	}
}
