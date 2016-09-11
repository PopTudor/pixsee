package com.marked.pixsee.friends.di;

import com.marked.pixsee.friends.FriendFragment;

import dagger.Component;
import dependencyInjection.components.ActivityComponent;
import dependencyInjection.scopes.FragmentScope;

/**
 * Created by Tudor Pop on 19-Mar-16.
 * Because this componend depends on appcomponent, you have to add the appcomponent to the builder
 * DaggerFriendsComponent.builder().appComponent(appcomponent).friendModule(new FriendModule(this)).build().inject(this);
 */
@FragmentScope
@Component(modules = {FriendModule.class}, dependencies = {ActivityComponent.class})
public interface FriendsComponent {
	void inject(FriendFragment friendFragment);
}
