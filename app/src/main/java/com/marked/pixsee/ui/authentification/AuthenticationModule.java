package com.marked.pixsee.ui.authentification;

import com.google.gson.Gson;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.injection.scopes.ActivityScope;
import com.marked.pixsee.networking.ServerConstants;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Tudor on 13-Jun-16.
 */
@Module
@ActivityScope
class AuthenticationModule {
	private AuthenticationContract.View mView;

	AuthenticationModule(AuthenticationContract.View view) {
		mView = view;
	}

	@ActivityScope
	@Provides
	AuthenticationContract.Presenter providePresenter(@Named(ServerConstants.SERVER) Retrofit retrofit,
	                                                  UserManager manager, Gson gson) {
		return new Presenter(mView, retrofit.create(AuthAPI.class), manager, gson);
	}
}
