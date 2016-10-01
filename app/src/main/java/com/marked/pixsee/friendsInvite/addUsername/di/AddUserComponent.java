package com.marked.pixsee.friendsInvite.addUsername.di;

import com.marked.pixsee.friendsInvite.addUsername.AddUsernameFragment;
import com.pixsee.di.components.ActivityComponent;
import com.pixsee.di.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by Tudor on 03-Jun-16.
 */
@Component(modules = AddUserModule.class,dependencies = ActivityComponent.class)
@FragmentScope
public interface AddUserComponent {
	void inject(AddUsernameFragment usernameFragment);
}
