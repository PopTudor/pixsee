package com.marked.pixsee.friends.di;

import android.content.Context;
import android.preference.PreferenceManager;

import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.repository.user.UserDatasource;
import com.marked.pixsee.data.repository.user.UserRepository;
import com.marked.pixsee.friends.FriendPresenter;
import com.marked.pixsee.data.repository.user.UserDiskDatasource;
import com.marked.pixsee.data.repository.user.UserNetworkDatasource;
import com.marked.pixsee.friends.FriendsContract;
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
	UserRepository provideRepository(Context application) {
		UserDatasource diskData = new UserDiskDatasource(PixyDatabase.getInstance(application));
		UserDatasource networkData = new UserNetworkDatasource(PreferenceManager.getDefaultSharedPreferences(application));

		return new UserRepository(diskData, networkData);
	}

	@Provides
	@PerFragment
	FriendsContract.Presenter provideFriendPresenter(UserRepository repository) {
		return new FriendPresenter(friendFragment, repository);
	}


}
