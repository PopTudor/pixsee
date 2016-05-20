package com.marked.pixsee.friends.data;

import android.support.annotation.NonNull;

import com.marked.pixsee.data.User;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 2016-05-19.
 * Main entry point for accessing user data.
 * <p>
 */
public interface FriendsDatasource {
	Observable<List<User>> getUsers();

	Observable<User> getUser(@NonNull User userId);

	void saveUser(@NonNull User user);
	void saveUser(@NonNull List<User> user);

	void refreshUsers();

	void deleteAllUsers();

	void deleteUsers(@NonNull User userId);
}
