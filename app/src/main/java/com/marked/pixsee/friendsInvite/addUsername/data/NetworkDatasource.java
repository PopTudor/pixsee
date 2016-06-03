package com.marked.pixsee.friendsInvite.addUsername.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.marked.pixsee.friends.data.User;
import com.marked.pixsee.networking.ServerConstants;

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
 * Created by Tudor on 03-Jun-16.
 */
public class NetworkDatasource implements Datasource {
	private final HttpLoggingInterceptor loggingInterceptor;
	private final OkHttpClient httpClient;
	private final Retrofit retrofit;
	private final Gson gson = new Gson();

	public NetworkDatasource() {
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

	}

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
}
