package com.marked.pixsee.ui.friendsInvite.addUsername;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.marked.pixsee.data.user.User;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by Tudor on 03-Jun-16.
 */
class Presenter implements AddUsernameContract.Presenter {
	private final WeakReference<AddUsernameContract.View> mView;
	private final Gson mGson;
	private final User mAppsUser;
	private final SearchAPI mSearchAPI;


	public Presenter(AddUsernameContract.View view, User appUser, SearchAPI mSearchAPI, Gson gson) {
		this.mView = new WeakReference<>(view);
		mGson = gson;
		this.mView.get().setPresenter(this);
		this.mAppsUser = appUser;
		this.mSearchAPI = mSearchAPI;
	}

	@Override
	public void attach() {

	}

	@Override
	public void detach() {

	}

	@Override
	public void search(@NonNull String username) {
		if (username.isEmpty())
			return;
		mSearchAPI.searchUsersByUsername(mAppsUser.getId(), username)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.flatMap(new Func1<JsonArray, Observable<JsonElement>>() {
					@Override
					public Observable<JsonElement> call(JsonArray jsonElements) {
						return Observable.from(jsonElements);
					}
				})
				.map(new Func1<JsonElement, Relationship>() {
					@Override
					public Relationship call(JsonElement jsonObject) {
						return mGson.fromJson(jsonObject, Relationship.class);
					}
				})
				.toList()
				.subscribe(new Action1<List<Relationship>>() {
					@Override
					public void call(List<Relationship> users) {
						mView.get().showUsers(users);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						throwable.printStackTrace();
						if (throwable instanceof SocketTimeoutException)
							mView.get().showNoInternetConnection();
					}
				});

	}

	@Override
	public void onClick(Relationship relationship, int position) {
		mSearchAPI.friendRequest(mAppsUser, relationship.user)
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
