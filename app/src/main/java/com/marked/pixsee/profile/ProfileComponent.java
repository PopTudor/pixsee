package com.marked.pixsee.profile;

import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by Tudor on 07-Jun-16.
 */
@Component(modules = ProfileModule.class, dependencies = ActivityComponent.class)
@FragmentScope
interface ProfileComponent {
	void inject(ProfileFragment profileFragment);
}
