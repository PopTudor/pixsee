package com.marked.pixsee.ui.main;

import com.marked.pixsee.injection.components.SessionComponent;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.injection.scopes.ActivityScope;

import dagger.Component;

/**
 * Created by Tudor on 2016-05-27.
 */
@Component(modules = {MainModule.class, ActivityModule.class}, dependencies = SessionComponent.class)
@ActivityScope
interface MainComponent {
	void inject(MainActivity mainActivity);
}
