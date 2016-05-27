package com.marked.pixsee.friends.di;

import android.content.Context;
import android.preference.PreferenceManager;

import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.friends.FriendPresenter;
import com.marked.pixsee.friends.data.FriendRepository;
import com.marked.pixsee.friends.data.datasource.FriendsDatasource;
import com.marked.pixsee.friends.data.datasource.FriendsDiskDatasource;
import com.marked.pixsee.friends.data.datasource.FriendsNetworkDatasource;
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
	FriendRepository provideRepository(Context application) {
		FriendsDatasource diskData = new FriendsDiskDatasource(PixyDatabase.getInstance(application));
		FriendsDatasource networkData = new FriendsNetworkDatasource(PreferenceManager.getDefaultSharedPreferences(application));

		return new FriendRepository(diskData, networkData);
	}

	@Provides
	@PerFragment
	FriendsContract.Presenter provideFriendPresenter(FriendRepository repository) {
		return new FriendPresenter(friendFragment, repository);
	}


}
