package com.marked.pixsee.friends.di;

import android.content.Context;

import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.friends.FriendFragment;
import com.marked.pixsee.friends.FriendViewModel;
import com.marked.pixsee.friends.data.FriendRepository;
import com.marked.pixsee.injection.scopes.PerFragment;

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
	FriendRepository provideFriendRepository(Context application){
		return new FriendRepository(PixyDatabase.getInstance(application));
	}

	@Provides
	@PerFragment
	FriendViewModel provideFriendViewModel(FriendRepository repository) {
		return new FriendViewModel(repository);
	}
}
