package com.marked.pixsee.data.user;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.List;

import rx.Observable;


/**
 * Created by Tudor on 23-Jul-16.
 */
public class FakeUserDatasource implements UserDatasource {

	@Override
	public Observable<List<User>> getUsers() {
		return getRandomUsers();
	}

	@Override
	public Observable<List<User>> getUsers(String byName) {
		return getRandomUsers();
	}

	private Observable<List<User>> getRandomUsers() {
		return Observable.just(User.getRandomUsers(10));
	}

	@Override
	public Observable<User> getUser(@NonNull User userId) {
		return Observable.from(User.getRandomUsers(1)).first();
	}

	@Override
	public User getUser(@NonNull String tablename) {
		return User.getRandomUsers(1).get(0);
	}

	@Override
	public Observable<JsonObject> saveUser(@NonNull User user) {
		return Observable.empty();
	}

	@Override
	public void saveUser(@NonNull List<User> user) {

	}

	@Override
	public Observable<List<User>> refreshUsers() {
		return getRandomUsers();
	}

	@Override
	public void deleteAllUsers() {

	}

	@Override
	public void deleteUsers(@NonNull User userId) {

	}

	@Override
	public void clear() {

	}
}
