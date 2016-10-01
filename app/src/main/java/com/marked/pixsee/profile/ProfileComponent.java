package com.marked.pixsee.profile;

import com.pixsee.di.components.ActivityComponent;
import com.pixsee.di.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by Tudor on 07-Jun-16.
 */
@Component(modules = ProfileModule.class, dependencies = ActivityComponent.class)
@FragmentScope
interface ProfileComponent {
	void inject(ProfileFragment profileFragment);
}
