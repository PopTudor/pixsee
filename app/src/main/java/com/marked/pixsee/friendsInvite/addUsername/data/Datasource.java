package com.marked.pixsee.friendsInvite.addUsername.data;

import com.marked.pixsee.friends.data.User;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 03-Jun-16.
 */
public interface Datasource<T> {
	/**
	 * Get users by email or username that starts with the specified string
	 * @param startingWith email or username starts with this parameter
	 * */
	Observable<List<User>> getUsers(String startingWith);
}
