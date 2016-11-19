package com.marked.pixsee.data.user;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.marked.pixsee.data.friends.FriendsAPI;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 2016-05-20.
 */
public class UserNetworkDatasource implements UserDatasource {
	private final FriendsAPI mFriendsAPI;
	private final Gson mGson;
	private final UserManager mUserManager;

	public UserNetworkDatasource(UserManager userManager, FriendsAPI friendsAPI, Gson gson) {
		this.mFriendsAPI = friendsAPI;
		this.mGson = gson;
		mUserManager = userManager;
	}

	/**
	 * Get users by email that start with the parameter
	 *
	 * @param startingWith the email starts with this string
	 */
	@Override
	public Observable<List<User>> getUsers(String startingWith) {
		return mFriendsAPI.getUsers(startingWith)
				       .subscribeOn(Schedulers.io())
				       .flatMap(new Func1<JsonArray, Observable<JsonElement>>() {
					       @Override
					       public Observable<JsonElement> call(JsonArray jsonElements) {
						       return Observable.from(jsonElements);
					       }
				       })
				       .map(new Func1<JsonElement, User>() {
					       @Override
					       public User call(JsonElement jsonElement) {
						       return mGson.fromJson(jsonElement, User.class);
					       }
				       })
				       .toList();
	}

	@Override
	public void clear() {

	}

	@Override
	public Observable<List<User>> getUsers() {
		return mFriendsAPI.getUsers(mUserManager.getAppUser().getId())
				       .subscribeOn(Schedulers.io())
				       .flatMap(new Func1<JsonArray, Observable<JsonElement>>() {
					       @Override
					       public Observable<JsonElement> call(JsonArray jsonElements) {
						       return Observable.from(jsonElements);
					       }
				       })
				       .map(new Func1<JsonElement, User>() {
					       @Override
					       public User call(JsonElement jsonElement) {
						       return mGson.fromJson(jsonElement, User.class);
					       }
				       })
				       .toList();
	}

	@Override
	public Observable<User> getUser(@NonNull User userId) {
		return null;
	}

	@Override
	public User getUser(@NonNull String tablename) {
		throw new IllegalArgumentException("This does not make sense because the the network does not have a table name. This method is " +
				"only needed for local datasource");
	}


	@Override
	public Observable<JsonObject> acceptFriendRequest(@NonNull User user) {
		return mFriendsAPI.friendAccepted(user.getId(), mUserManager.getAppUser().getId())
				.subscribeOn(Schedulers.io());
	}

	@Override
	public Observable<JsonObject> rejectFriendRequest(@NonNull User user) {
		return mFriendsAPI.friendRejected(user.getId(), mUserManager.getAppUser().getId())
				       .subscribeOn(Schedulers.io());
	}

	@Override
	public void saveUser(@NonNull List<User> user) {

	}

	@Override
	public Observable<List<User>> refreshUsers() {
		return getUsers();
	}

	@Override
	public void deleteAllUsers() {

	}

	@Override
	public void deleteUsers(@NonNull User userId) {

	}
}
