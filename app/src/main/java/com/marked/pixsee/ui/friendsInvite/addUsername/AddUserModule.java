package com.marked.pixsee.ui.friendsInvite.addUsername;

import com.google.gson.Gson;
import com.marked.pixsee.data.friends.FriendsAPI;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.injection.scopes.FragmentScope;
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
	AddUsernameContract.Presenter providesPresenter(@Named(ServerConstants.SERVER) Retrofit retrofit, UserManager userManager, Gson gson) {
		FriendsAPI searchAPI = retrofit.create(FriendsAPI.class);
		return new Presenter(view, userManager.getAppUser(), searchAPI, gson);
	}
}
