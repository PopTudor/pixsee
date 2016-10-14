package com.marked.pixsee.features.authentification.di;

import com.marked.pixsee.di.components.AppComponent;
import com.marked.pixsee.di.modules.ActivityModule;
import com.marked.pixsee.di.scopes.ActivityScope;
import com.marked.pixsee.features.authentification.AuthenticationActivity;

import dagger.Component;

/**
 * Created by Tudor on 13-Jun-16.
 */
@Component(modules = {AuthenticationModule.class, ActivityModule.class},dependencies = {AppComponent.class})
@ActivityScope
public interface AuthenticationComponent {
	void inject(AuthenticationActivity authenticationActivity);
}
