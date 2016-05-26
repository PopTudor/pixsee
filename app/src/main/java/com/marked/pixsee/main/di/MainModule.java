package com.marked.pixsee.main.di;

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
	public MainContract.Presenter providesPresenter(){
		MainContract.Presenter presenter = new MainPresenter(activity);
		return presenter;
	}
}
