package com.marked.pixsee.main;

import com.marked.pixsee.model.user.UserDatasource;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;
import dependencyInjection.scopes.ActivityScope;
import dependencyInjection.scopes.Repository;

/**
 * Created by Tudor on 24-Jul-16.
 */
@Module
public class FakeMainModule {
	@Provides
	@ActivityScope
	public MainContract.Presenter providesPresenter(@Repository UserDatasource userRepository){
		return Mockito.mock(MainPresenter.class);
	}
}
