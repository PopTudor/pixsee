package com.marked.pixsee.profile;

import dagger.Component;
import dependencyInjection.components.ActivityComponent;
import dependencyInjection.scopes.FragmentScope;

/**
 * Created by Tudor on 07-Jun-16.
 */
@Component(modules = ProfileModule.class, dependencies = ActivityComponent.class)
@FragmentScope
interface ProfileComponent {
	void inject(ProfileFragment profileFragment);
}
