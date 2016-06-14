package com.marked.pixsee.login.di;

import android.content.SharedPreferences;

import com.marked.pixsee.data.repository.user.UserRepository;
import com.marked.pixsee.injection.scopes.PerActivity;
import com.marked.pixsee.login.LoginContract;
import com.marked.pixsee.login.Presenter;
import com.marked.pixsee.networking.ServerConstants;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Tudor on 14-Jun-16.
 */
@Module
public class LoginModule {
	private LoginContract.View mView;

	public LoginModule(LoginContract.View view) {
		mView = view;
	}

	@Provides
	@PerActivity
	LoginContract.Presenter providePresenter(@Named(ServerConstants.SERVER) Retrofit retrofit,
	                                         UserRepository repository,
	                                         SharedPreferences preferences){
		return new Presenter(mView,retrofit,repository,preferences);
	}
}
