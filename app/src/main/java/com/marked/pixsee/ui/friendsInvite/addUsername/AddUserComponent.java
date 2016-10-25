package com.marked.pixsee.ui.friendsInvite.addUsername;

import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by Tudor on 03-Jun-16.
 */
@Component(modules = AddUserModule.class,dependencies = ActivityComponent.class)
@FragmentScope
interface AddUserComponent {
	void inject(AddUsernameFragment usernameFragment);
}
