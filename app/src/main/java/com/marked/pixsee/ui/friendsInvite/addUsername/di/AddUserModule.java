package com.marked.pixsee.ui.friendsInvite.addUsername.di;

import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.injection.scopes.Repository;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.ui.friendsInvite.addUsername.AddUsernameContract;
import com.marked.pixsee.ui.friendsInvite.addUsername.FriendRequestAPI;

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
	AddUsernameContract.Presenter providesPresenter(@Repository UserDatasource repository, @Named(ServerConstants.SERVER) Retrofit
			                                                                                       retrofit, UserManager userManager) {
		FriendRequestAPI friendRequestAPI = retrofit.create(FriendRequestAPI.class);
		return new Presenter(view, repository, userManager.getAppUser(), friendRequestAPI);
	}
}
