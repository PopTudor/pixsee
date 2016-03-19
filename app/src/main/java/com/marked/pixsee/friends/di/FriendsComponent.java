package com.marked.pixsee.friends.di;

import com.marked.pixsee.di.components.AppComponent;
import com.marked.pixsee.di.scopes.PerFragment;
import com.marked.pixsee.friends.FriendFragment;

import dagger.Component;

/**
 * Created by Tudor Pop on 19-Mar-16.
 */
@PerFragment
@Component(modules = {FriendModule.class}, dependencies = {AppComponent.class})
public interface FriendsComponent {
	void inject(FriendFragment fragment);
}
