package com.marked.pixsee.data.repository.user;

import android.support.annotation.NonNull;

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

	Observable saveUser(@NonNull User user);

	Observable saveAppUser(@NonNull User user);

	void saveUser(@NonNull List<User> user);

	Observable<List<User>> refreshUsers();

	void deleteAllUsers();

	void deleteUsers(@NonNull User userId);

	void clear();
}
