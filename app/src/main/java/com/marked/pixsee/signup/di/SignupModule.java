package com.marked.pixsee.signup.di;

import android.content.SharedPreferences;

import com.marked.pixsee.data.repository.user.UserRepository;
import com.marked.pixsee.injection.scopes.PerActivity;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.signup.Presenter;
import com.marked.pixsee.signup.SignUpContract;

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
	private SignUpContract.View mView;

	public SignupModule(SignUpContract.View view) {
		mView = view;
	}

	@PerActivity
	@Provides
	SignUpContract.Presenter providePresenter(@Named(ServerConstants.SERVER) Retrofit retrofit, UserRepository datasource,
	                                          SharedPreferences preferences) {
		return new Presenter(mView, retrofit,datasource,preferences);
	}
}
