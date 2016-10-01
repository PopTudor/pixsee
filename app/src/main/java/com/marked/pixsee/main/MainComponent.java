package com.marked.pixsee.main;

import com.pixsee.di.modules.ActivityModule;
import com.pixsee.di.scopes.ActivityScope;

import dagger.Component;

/**
 * Created by Tudor on 2016-05-27.
 */
@Component(modules = {MainModule.class, ActivityModule.class})
@ActivityScope
interface MainComponent {
	void inject(MainActivity mainActivity);
}
