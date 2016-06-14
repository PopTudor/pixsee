package com.marked.pixsee.login.di;

import com.marked.pixsee.injection.components.AppComponent;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.injection.scopes.PerActivity;
import com.marked.pixsee.login.LogInActivity;

import dagger.Component;

/**
 * Created by Tudor on 14-Jun-16.
 */
@Component(modules = {LoginModule.class, ActivityModule.class},dependencies = AppComponent.class)
@PerActivity
public interface LoginComponent {
	void inject(LogInActivity logInActivity);
}
