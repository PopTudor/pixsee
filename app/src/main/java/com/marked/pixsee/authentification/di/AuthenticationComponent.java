package com.marked.pixsee.authentification.di;

import com.marked.pixsee.authentification.AuthenticationActivity;

import dagger.Component;
import dependencyInjection.components.AppComponent;
import dependencyInjection.modules.ActivityModule;
import dependencyInjection.scopes.ActivityScope;

/**
 * Created by Tudor on 13-Jun-16.
 */
@Component(modules = {AuthenticationModule.class, ActivityModule.class},dependencies = {AppComponent.class})
@ActivityScope
public interface AuthenticationComponent {
	void inject(AuthenticationActivity authenticationActivity);
}
