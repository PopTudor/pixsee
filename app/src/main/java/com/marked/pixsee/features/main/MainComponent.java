package com.marked.pixsee.features.main;

import com.marked.pixsee.di.components.AppComponent;
import com.marked.pixsee.di.modules.ActivityModule;
import com.marked.pixsee.di.scopes.ActivityScope;

import dagger.Component;

/**
 * Created by Tudor on 2016-05-27.
 */
@Component(modules = {MainModule.class, ActivityModule.class}, dependencies = AppComponent.class)
@ActivityScope
interface MainComponent {
	void inject(MainActivity mainActivity);
}
