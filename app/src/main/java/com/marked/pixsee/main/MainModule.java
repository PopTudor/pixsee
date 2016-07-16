package com.marked.pixsee.main;

import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.injection.Repository;
import com.marked.pixsee.injection.scopes.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 2016-05-27.
 */
@Module
class MainModule {
	private MainActivity activity;

	public MainModule(MainActivity activity) {
		this.activity = activity;
	}
	@Provides
	@ActivityScope
	public MainContract.Presenter providesPresenter(@Repository UserDatasource userRepository){
		MainContract.Presenter presenter = new MainPresenter(activity, userRepository);
		return presenter;
	}
}
