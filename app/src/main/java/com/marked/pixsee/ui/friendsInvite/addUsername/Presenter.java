package com.marked.pixsee.ui.friendsInvite.addUsername;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.marked.pixsee.data.friends.FriendsAPI;
import com.marked.pixsee.data.user.User;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;

import rx.Observable;
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
	private final FriendsAPI mFriendsAPI;


	public Presenter(AddUsernameContract.View view, User appUser, FriendsAPI mSearchAPI, Gson gson) {
		this.mView = new WeakReference<>(view);
		mGson = gson;
		this.mView.get().setPresenter(this);
		this.mAppsUser = appUser;
		this.mFriendsAPI = mSearchAPI;
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
		mFriendsAPI.searchUsersByUsername(mAppsUser.getId(), username)
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
	public void onClick(final Relationship relationship, int position) {
		if (relationship.status.equals(Relationship.Status.USER))
			mFriendsAPI.friendRequest(mAppsUser, relationship.user)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Action1<Relationship>() {
						@Override
						public void call(Relationship relationship1) {
							mView.get().update(relationship1);
						}
					}, new Action1<Throwable>() {
						@Override
						public void call(Throwable throwable) {
							throwable.printStackTrace();
						}
					});
		else if (relationship.status.equals(Relationship.Status.FRIEND) || relationship.status.equals(Relationship.Status.SENT_FRIEND_REQUEST)) {
			mFriendsAPI.unfriend(mAppsUser.getId(), relationship.getUser().getId())
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.doOnNext(new Action1<Relationship>() {
						@Override
						public void call(Relationship relationship1) {
							mView.get().update(relationship1);
						}
					})
					.doOnError(new Action1<Throwable>() {
						@Override
						public void call(Throwable throwable) {
							throwable.printStackTrace();
						}
					})
					.subscribe();
		}

	}

	private Relationship convertToRelationship(JsonObject jsonObject) {
		return mGson.fromJson(jsonObject, Relationship.class);
	}
}
