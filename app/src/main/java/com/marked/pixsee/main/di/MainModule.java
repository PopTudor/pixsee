package com.marked.pixsee.main.di;

import com.marked.pixsee.data.repository.user.UserRepository;
import com.marked.pixsee.injection.scopes.PerActivity;
import com.marked.pixsee.main.MainActivity;
import com.marked.pixsee.main.MainContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 2016-05-27.
 */
@Module
public class MainModule {
	private MainActivity activity;

	public MainModule(MainActivity activity) {
		this.activity = activity;
	}
	@Provides
	@PerActivity
	public MainContract.Presenter providesPresenter(UserRepository userRepository){
		MainContract.Presenter presenter = new MainPresenter(activity, userRepository);
		return presenter;
	}
}
