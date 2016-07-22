package com.marked.pixsee.friendsInvite.addUsername.di;

import com.google.gson.JsonObject;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.friendsInvite.addUsername.AddUsernameContract;
import com.marked.pixsee.friendsInvite.addUsername.AddUserAPI;

import org.jetbrains.annotations.NotNull;

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
	private final AddUserAPI mAddUserAPI;


	public Presenter(AddUsernameContract.View view, UserDatasource repository, User appUser, AddUserAPI mAddUserAPI) {
		this.mView = new WeakReference<>(view);
		this.mView.get().setPresenter(this);
		this.repository = repository;
		this.mAppsUser = appUser;
		this.mAddUserAPI = mAddUserAPI;
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
		mAddUserAPI.friendRequest(mAppsUser,user.getToken())
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
