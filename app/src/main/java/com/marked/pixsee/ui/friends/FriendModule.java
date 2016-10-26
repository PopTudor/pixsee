package com.marked.pixsee.ui.friends;

import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.injection.scopes.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor Pop on 18-Mar-16.
 */
@Module
class FriendModule {
	private FriendsContract.View friendFragment;

	FriendModule(FriendsContract.View friendFragment) {
		this.friendFragment = friendFragment;
	}

	@Provides
	@FragmentScope
	FriendsContract.Presenter provideFriendPresenter(@Repository UserDatasource repository) {
		FriendsContract.Presenter presenter = new FriendPresenter(friendFragment, repository);
		friendFragment.setPresenter(presenter);
		return presenter;
	}
}
