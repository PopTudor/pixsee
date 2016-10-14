package com.marked.pixsee.features.friends.di;

import com.marked.pixsee.di.components.ActivityComponent;
import com.marked.pixsee.di.scopes.FragmentScope;
import com.marked.pixsee.features.friends.FriendFragment;

import dagger.Component;

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
