package com.marked.pixsee.ui.friendsInvite.addUsername;

import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.injection.scopes.Repository;
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
class AddUserModule {
	private AddUsernameContract.View view;

	AddUserModule(AddUsernameContract.View view) {
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