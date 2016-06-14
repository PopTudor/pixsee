package com.marked.pixsee.registration.di;

import android.content.SharedPreferences;

import com.marked.pixsee.data.repository.user.UserRepository;
import com.marked.pixsee.injection.scopes.PerActivity;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.registration.Presenter;
import com.marked.pixsee.registration.RegistrationContract;

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
	private RegistrationContract.View mView;

	public SignupModule(RegistrationContract.View view) {
		mView = view;
	}

	@PerActivity
	@Provides
	RegistrationContract.Presenter providePresenter(@Named(ServerConstants.SERVER) Retrofit retrofit, UserRepository datasource,
	                                                SharedPreferences preferences) {
		return new Presenter(mView, retrofit,datasource,preferences);
	}
}
