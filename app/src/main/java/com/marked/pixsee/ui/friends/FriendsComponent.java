package com.marked.pixsee.ui.friends;

import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by Tudor Pop on 19-Mar-16.
 * Because this componend depends on appcomponent, you have to add the appcomponent to the builder
 * DaggerFriendsComponent.builder().appComponent(appcomponent).friendModule(new FriendModule(this)).build().inject(this);
 */
@FragmentScope
@Component(modules = {FriendModule.class}, dependencies = {ActivityComponent.class})
interface FriendsComponent {
	void inject(FriendFragment friendFragment);
}
