package com.marked.pixsee.data.user;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.ui.friends.data.FriendContractDB;
import com.marked.pixsee.ui.friends.data.FriendsAPI;
import com.marked.pixsee.ui.friendsInvite.addUsername.FriendRequestAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 2016-05-20.
 */
public class UserNetworkDatasource implements UserDatasource {
	private final Retrofit mRetrofit;
	private final Gson mGson;
	private final UserManager mUserManager;

	public UserNetworkDatasource(UserManager userManager, Retrofit retrofit, Gson gson) {
		this.mRetrofit = retrofit;
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
		return Observable.empty();
	}

	@Override
	public Observable<List<User>> getFriendsWithEmail(String startingWithEmail) {
		return mRetrofit.create(FriendRequestAPI.class)
				       .getFriendsWithEmail(startingWithEmail)
				       .subscribeOn(Schedulers.io())
				       .map(new Func1<JsonObject, JsonArray>() {
					       @Override
					       public JsonArray call(JsonObject jsonObject) {
						       return jsonObject.get(ServerConstants.USERS).getAsJsonArray();
					       }
				       })
				       .flatMap(new Func1<JsonArray, Observable<JsonElement>>() {
					       @Override
					       public Observable<JsonElement> call(JsonArray jsonElements) {
						       return Observable.from(jsonElements);
					       }
				       })
				       .map(new Func1<JsonElement, User>() {
					       @Override
					       public User call(JsonElement jsonObject) {
						       return mGson.fromJson(jsonObject.toString(), User.class);
					       }
				       })
				       .toList();
	}

	@Override
	public void clear() {

	}

	@Override
	public Observable<List<User>> getUsers() {
		return mRetrofit.create(FriendsAPI.class)
				       .getUsers(mUserManager.getAppUser().getUserID())
				.subscribeOn(Schedulers.io())
				.map(new Func1<JsonObject, JsonArray>() {
					@Override
					public JsonArray call(JsonObject jsonObject) {
						return jsonObject.getAsJsonArray(FriendContractDB.TABLE_NAME);
					}
				})
				.map(new Func1<JsonArray, List<User>>() {
					@Override
					public List<User> call(JsonArray jsonElements) {
						final List<User> cache = new ArrayList<User>();
						for (JsonElement it : jsonElements)
							cache.add(mGson.fromJson(it.toString(), User.class));
						return cache;
					}
				});
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
	public Observable<JsonObject> saveUser(@NonNull User user) {
		return mRetrofit.create(FriendsAPI.class)
				       .friendAccepted(user.getUserID(), mUserManager.getAppUser().getUserID())
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
