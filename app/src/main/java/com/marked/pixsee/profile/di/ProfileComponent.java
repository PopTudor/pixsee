package com.marked.pixsee.profile.di;

import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.scopes.PerFragment;
import com.marked.pixsee.profile.ProfileFragment;

import dagger.Component;

/**
 * Created by Tudor on 07-Jun-16.
 */
@Component(modules = ProfileModule.class, dependencies = ActivityComponent.class)
@PerFragment
public interface ProfileComponent {
	void inject(ProfileFragment profileFragment);
}
