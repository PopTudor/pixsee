package com.marked.pixsee.friends.di;

import android.app.Application;

import com.marked.pixsee.di.scopes.PerFragment;
import com.marked.pixsee.friends.FriendFragment;
import com.marked.pixsee.friends.FriendPresenter;
import com.marked.pixsee.friends.data.FriendRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor Pop on 18-Mar-16.
 */
@Module
public class FriendModule {
	FriendFragment friendFragment;

	public FriendModule(FriendFragment friendFragment) {
		this.friendFragment = friendFragment;
	}

	@Provides
	@PerFragment
	FriendRepository provideFriendRepository(Application application){
		return new FriendRepository(application);
	}

	@Provides
	@PerFragment
	FriendPresenter provideFriendPresenter(FriendRepository repository) {
		return new FriendPresenter(repository, friendFragment);
	}
}
