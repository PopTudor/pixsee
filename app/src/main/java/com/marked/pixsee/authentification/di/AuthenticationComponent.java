package com.marked.pixsee.authentification.di;

import com.marked.pixsee.authentification.AuthenticationActivity;
import com.marked.pixsee.injection.components.AppComponent;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.injection.scopes.PerActivity;

import dagger.Component;

/**
 * Created by Tudor on 13-Jun-16.
 */
@Component(modules = {AuthenticationModule.class, ActivityModule.class},dependencies = {AppComponent.class})
@PerActivity
public interface AuthenticationComponent {
	void inject(AuthenticationActivity authenticationActivity);
}
