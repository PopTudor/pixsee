package com.marked.pixsee.features.friendsInvite.addUsername.di;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.di.scopes.FragmentScope;
import com.marked.pixsee.di.scopes.Repository;
import com.marked.pixsee.features.friendsInvite.addUsername.AddUserAPI;
import com.marked.pixsee.features.friendsInvite.addUsername.AddUsernameContract;
import com.marked.pixsee.networking.ServerConstants;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Tudor on 03-Jun-16.
 */
@Module
@FragmentScope
public class AddUserModule {
	private AddUsernameContract.View view;

	public AddUserModule(AddUsernameContract.View view) {
		this.view = view;
	}

	@Provides
	@FragmentScope
	AddUsernameContract.Presenter providesPresenter(@Repository UserDatasource repository, @Named(ServerConstants.SERVER) Retrofit retrofit) {
		User user = repository.getUser(DatabaseContract.AppsUser.TABLE_NAME);
		AddUserAPI addUserAPI = retrofit.create(AddUserAPI.class);
		return new Presenter(view, repository, user, addUserAPI);
	}
}
