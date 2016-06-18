package com.marked.pixsee.data.repository.user;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.marked.pixsee.friends.data.FriendContractDB;
import com.marked.pixsee.friends.data.FriendsAPI;
import com.marked.pixsee.friendsInvite.addUsername.data.AddUserAPI;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.utility.GCMConstants;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 2016-05-20.
 */
public class UserNetworkDatasource implements UserDatasource {
	private final HttpLoggingInterceptor loggingInterceptor;
	private final OkHttpClient httpClient;
	private final Retrofit retrofit;
	private final String userid;
	private final Gson gson = new Gson();

	public UserNetworkDatasource(SharedPreferences sharedPreferences) {
		loggingInterceptor = new HttpLoggingInterceptor();
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		httpClient = new OkHttpClient.Builder()
				.addInterceptor(loggingInterceptor)
				.build();
		retrofit = new Retrofit.Builder()
				.baseUrl(ServerConstants.SERVER)
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.client(httpClient)
				.build();

		userid = sharedPreferences.getString(GCMConstants.USER_ID, "");
	}

	/**
	 * Get users by email that start with the parameter
	 *
	 * @param startingWith the email starts with this string
	 */
	@Override
	public Observable<List<User>> getUsers(String startingWith) {
		return retrofit.create(AddUserAPI.class)
				.getFriendsWithEmail(startingWith)
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
						return gson.fromJson(jsonObject.toString(), User.class);
					}
				})
				.toList();
	}

	@Override
	public void clear() {

	}

	@Override
	public Observable<List<User>> getUsers() {
		return retrofit.create(FriendsAPI.class)
				.getFriendsOfUser(userid)
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
							cache.add(gson.fromJson(it.toString(), User.class));
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
		return retrofit.create(FriendsAPI.class)
				.friendAccepted(user.getUserID(),userid)
				.subscribeOn(Schedulers.io());
	}

	@Override
	public Observable saveAppUser(@NonNull User user) {
		return Observable.empty();
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
