package com.marked.pixsee.features.profile;

import com.marked.pixsee.di.components.ActivityComponent;
import com.marked.pixsee.di.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by Tudor on 07-Jun-16.
 */
@Component(modules = ProfileModule.class, dependencies = ActivityComponent.class)
@FragmentScope
interface ProfileComponent {
	void inject(ProfileFragment profileFragment);
}
