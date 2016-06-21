package com.marked.pixsee.profile.di;

import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.profile.ProfileFragment;

import dagger.Component;

/**
 * Created by Tudor on 07-Jun-16.
 */
@Component(modules = ProfileModule.class, dependencies = ActivityComponent.class)
@FragmentScope
public interface ProfileComponent {
	void inject(ProfileFragment profileFragment);
}
