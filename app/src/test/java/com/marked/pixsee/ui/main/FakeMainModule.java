package com.marked.pixsee.ui.main;

import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.injection.scopes.ActivityScope;
import com.marked.pixsee.injection.scopes.Repository;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

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
