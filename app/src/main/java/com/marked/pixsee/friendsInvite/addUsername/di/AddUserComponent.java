package com.marked.pixsee.friendsInvite.addUsername.di;

import com.marked.pixsee.friendsInvite.addUsername.AddUsernameFragment;
import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.scopes.PerFragment;

import dagger.Component;

/**
 * Created by Tudor on 03-Jun-16.
 */
@Component(modules = AddUserModule.class,dependencies = ActivityComponent.class)
@PerFragment
public interface AddUserComponent {
	void inject(AddUsernameFragment usernameFragment);
}
