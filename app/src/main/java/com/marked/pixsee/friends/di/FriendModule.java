package com.marked.pixsee.friends.di;

import android.content.Context;
import android.preference.PreferenceManager;

import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.friends.data.FriendRepository;
import com.marked.pixsee.friends.data.FriendsLocalDatasource;
import com.marked.pixsee.friends.data.FriendsRemoteDatasource;
import com.marked.pixsee.friends.friends.FriendPresenter;
import com.marked.pixsee.friends.friends.FriendsContract;
import com.marked.pixsee.injection.scopes.PerFragment;

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
	@PerFragment
	FriendRepository provideRepository(Context application) {
		FriendsLocalDatasource friendsLocalDatasource = new FriendsLocalDatasource(PixyDatabase.getInstance(application));
		FriendsRemoteDatasource friendsRemoteDatasource = new FriendsRemoteDatasource(PreferenceManager.getDefaultSharedPreferences(application));

		return new FriendRepository(friendsLocalDatasource, friendsRemoteDatasource);
	}

	@Provides
	@PerFragment
	FriendPresenter provideFriendPresenter(FriendRepository repository) {
		return new FriendPresenter(friendFragment, repository);
	}


}
