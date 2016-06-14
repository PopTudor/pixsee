package com.marked.pixsee.signup.di;

import com.marked.pixsee.injection.components.AppComponent;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.injection.scopes.PerActivity;
import com.marked.pixsee.signup.SignUpActivity;

import dagger.Component;

/**
 * Created by Tudor on 13-Jun-16.
 */
@Component(modules = {SignupModule.class, ActivityModule.class},dependencies = {AppComponent.class})
@PerActivity
public interface SignupComponent {
	void inject(SignUpActivity signUpActivity);
}
