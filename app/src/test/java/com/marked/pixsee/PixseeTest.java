package com.marked.pixsee;

import com.pixsee.di.components.AppComponent;
import com.pixsee.di.components.DaggerFakeAppComponent;
import com.pixsee.di.modules.FakeAppModule;

/**
 * Created by Tudor on 22-Jul-16.
 */
public class PixseeTest extends Pixsee{
	AppComponent mAppComponentFake;

	@Override
	public void onCreate() {
		super.onCreate();
		mAppComponentFake = DaggerFakeAppComponent.builder().fakeAppModule(new FakeAppModule(this)).build();
	}

	@Override
	public AppComponent getAppComponent() {
		return mAppComponentFake;
	}
}