package com.marked.pixsee.main;

import android.content.Context;

import com.marked.pixsee.model.user.UserDatasource;
import com.pixsee.di.scopes.ActivityScope;
import com.pixsee.di.scopes.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 2016-05-27.
 */
@Module
class MainModule {
	@Provides
	@ActivityScope
	public MainContract.Presenter providesPresenter(Context activity, @Repository UserDatasource userRepository) {
		MainContract.Presenter presenter = new MainPresenter((MainActivity) activity, userRepository);
		return presenter;
	}
}
