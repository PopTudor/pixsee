package com.marked.pixsee;

import com.marked.pixsee.injection.components.AppComponent;
import com.marked.pixsee.injection.components.DaggerAppComponent;
import com.marked.pixsee.injection.modules.AppModule;

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