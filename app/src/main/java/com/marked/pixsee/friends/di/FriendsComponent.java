package com.marked.pixsee.friends.di;

import com.marked.pixsee.di.components.ActivityComponent;
import com.marked.pixsee.di.scopes.PerFragment;
import com.marked.pixsee.friends.FriendFragment;
import com.marked.pixsee.friends.FriendViewModel;
import com.marked.pixsee.friends.FriendsActivity;

import dagger.Component;

/**
 * Created by Tudor Pop on 19-Mar-16.
 * Because this componend depends on appcomponent, you have to add the appcomponent to the builder
 * DaggerFriendsComponent.builder().appComponent(appcomponent).friendModule(new FriendModule(this)).build().inject(this);
 */
@PerFragment
@Component(modules = {FriendModule.class}, dependencies = {ActivityComponent.class})
public interface FriendsComponent {
	void inject(FriendFragment fragment);

	void inject(FriendsActivity friendsActivity);

	void inject(FriendViewModel viewModel);
}
