package com.marked.pixsee.friendsInvite.addUsername.di;

import com.marked.pixsee.friendsInvite.addUsername.AddUserAPI;
import com.marked.pixsee.friendsInvite.addUsername.AddUsernameContract;
import com.marked.pixsee.model.database.DatabaseContract;
import com.marked.pixsee.model.user.User;
import com.marked.pixsee.model.user.UserDatasource;
import com.marked.pixsee.networking.ServerConstants;
import com.pixsee.di.scopes.FragmentScope;
import com.pixsee.di.scopes.Repository;

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
