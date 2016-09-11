package com.marked.pixsee.friendsInvite.addUsername.di;

import com.marked.pixsee.friendsInvite.addUsername.AddUsernameFragment;

import dagger.Component;
import dependencyInjection.components.ActivityComponent;
import dependencyInjection.scopes.FragmentScope;

/**
 * Created by Tudor on 03-Jun-16.
 */
@Component(modules = AddUserModule.class,dependencies = ActivityComponent.class)
@FragmentScope
public interface AddUserComponent {
	void inject(AddUsernameFragment usernameFragment);
}
