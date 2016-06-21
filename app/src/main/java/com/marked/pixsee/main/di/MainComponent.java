package com.marked.pixsee.main.di;

import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.injection.scopes.ActivityScope;
import com.marked.pixsee.main.MainActivity;

import dagger.Component;

/**
 * Created by Tudor on 2016-05-27.
 */
@Component(modules = {MainModule.class, ActivityModule.class})
@ActivityScope
public interface MainComponent {
	void inject(MainActivity mainActivity);
}
