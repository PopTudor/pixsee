package com.marked.pixsee.ui.friends.di;

import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.injection.scopes.Repository;
import com.marked.pixsee.ui.friends.FriendPresenter;
import com.marked.pixsee.ui.friends.FriendsContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor Pop on 18-Mar-16.
 */
@Module
public class FriendModule {
	FriendsContract.View friendFragment;

	public FriendModule(FriendsContract.View friendFragment) {
		this.friendFragment = friendFragment;
	}

	@Provides
	@FragmentScope
	FriendsContract.Presenter provideFriendPresenter(@Repository UserDatasource repository) {
		return new FriendPresenter(friendFragment, repository);
	}
}
