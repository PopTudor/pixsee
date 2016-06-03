package com.marked.pixsee.friendsInvite.addUsername.data;

import com.google.repacked.antlr.v4.runtime.misc.NotNull;
import com.marked.pixsee.friends.data.User;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 03-Jun-16.
 */
public class Repository implements Datasource {
	private Datasource networkDatasourceDatasource;

	public Repository(@NotNull Datasource networkDatasourceDatasource) {
		this.networkDatasourceDatasource = networkDatasourceDatasource;
	}

	@Override
	public Observable<List<User>> getUsers(String startingWith) {
		return networkDatasourceDatasource.getUsers(startingWith);
	}
}
