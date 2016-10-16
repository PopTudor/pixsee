package com.marked.pixsee;

import com.marked.pixsee.di.components.AppComponent;
import com.marked.pixsee.di.components.DaggerAppComponent;
import com.marked.pixsee.di.modules.AppModule;

/**
 * Created by Tudor on 22-Jul-16.
 */
public class PixseeTest extends Pixsee{
	AppComponent mAppComponentFake;

	@Override
	public void onCreate() {
		super.onCreate();
		mAppComponentFake = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
	}

	@Override
	public AppComponent getAppComponent() {
		return mAppComponentFake;
	}
}