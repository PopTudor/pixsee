package com.marked.pixsee.main;

import dagger.Component;
import dependencyInjection.modules.ActivityModule;
import dependencyInjection.scopes.ActivityScope;

/**
 * Created by Tudor on 2016-05-27.
 */
@Component(modules = {MainModule.class, ActivityModule.class})
@ActivityScope
interface MainComponent {
	void inject(MainActivity mainActivity);
}
