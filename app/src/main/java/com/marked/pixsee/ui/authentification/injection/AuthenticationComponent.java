package com.marked.pixsee.ui.authentification.injection;

import com.marked.pixsee.injection.components.SessionComponent;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.injection.scopes.ActivityScope;
import com.marked.pixsee.ui.authentification.AuthenticationActivity;

import dagger.Component;

/**
 * Created by Tudor on 13-Jun-16.
 */
@Component(modules = {AuthenticationModule.class, ActivityModule.class}, dependencies = SessionComponent.class)
@ActivityScope
public interface AuthenticationComponent {
	void inject(AuthenticationActivity authenticationActivity);
}
