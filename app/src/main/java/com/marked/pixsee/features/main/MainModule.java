package com.marked.pixsee.features.main;

import android.content.Context;

import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.di.scopes.ActivityScope;
import com.marked.pixsee.di.scopes.Repository;

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
