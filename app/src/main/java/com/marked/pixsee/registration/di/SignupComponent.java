package com.marked.pixsee.registration.di;

import com.marked.pixsee.injection.components.AppComponent;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.injection.scopes.PerActivity;
import com.marked.pixsee.registration.RegistrationActivity;

import dagger.Component;

/**
 * Created by Tudor on 13-Jun-16.
 */
@Component(modules = {SignupModule.class, ActivityModule.class},dependencies = {AppComponent.class})
@PerActivity
public interface SignupComponent {
	void inject(RegistrationActivity registrationActivity);
}
