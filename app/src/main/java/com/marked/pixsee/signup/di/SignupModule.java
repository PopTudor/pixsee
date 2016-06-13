package com.marked.pixsee.signup.di;

import com.marked.pixsee.injection.scopes.PerActivity;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.signup.Presenter;
import com.marked.pixsee.signup.SignupContract;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Tudor on 13-Jun-16.
 */
@Module
@PerActivity
public class SignupModule {
	private SignupContract.View mView;

	public SignupModule(SignupContract.View view) {
		mView = view;
	}

	@PerActivity
	@Provides
	SignupContract.Presenter providePresenter(@Named(ServerConstants.SERVER) Retrofit retrofit) {
		return new Presenter(mView, retrofit);
	}
}
