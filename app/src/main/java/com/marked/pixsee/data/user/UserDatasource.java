package com.marked.pixsee.data.user;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 2016-05-19.
 * Main entry point for accessing user data.
 * <p>
 */
public interface UserDatasource {
	Observable<List<User>> getUsers();

	Observable<List<User>> getUsers(String byName);

	Observable<User> getUser(@NonNull User userId);

	User getUser(@NonNull String tablename);

	Observable<JsonObject> acceptFriendRequest(@NonNull User user);

	Observable<JsonObject> rejectFriendRequest(@NonNull User user);

	void saveUser(@NonNull List<User> user);

	Observable<List<User>> refreshUsers();

	void deleteAllUsers();

	void deleteUsers(@NonNull User userId);

	void clear();
}
