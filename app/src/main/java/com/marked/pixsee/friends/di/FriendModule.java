package com.marked.pixsee.friends.di;

import com.marked.pixsee.friends.FriendPresenter;
import com.marked.pixsee.friends.FriendsContract;
import com.marked.pixsee.model.user.UserDatasource;

import dagger.Module;
import dagger.Provides;
import dependencyInjection.scopes.FragmentScope;
import dependencyInjection.scopes.Repository;

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
