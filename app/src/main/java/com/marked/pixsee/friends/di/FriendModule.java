package com.marked.pixsee.friends.di;

import android.content.Context;

import com.marked.pixsee.di.scopes.PerFragment;
import com.marked.pixsee.friends.FriendFragment;
import com.marked.pixsee.friends.FriendViewModel;
import com.marked.pixsee.friends.commands.FabCommand;
import com.marked.pixsee.friends.commands.OpenCameraCommand;
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
	FriendRepository provideFriendRepository(Context application){
		return new FriendRepository(application);
	}

	@Provides
	@PerFragment
	FriendViewModel provideFriendViewModel(FriendRepository repository,FabCommand fabCommand,OpenCameraCommand cameraCommand) {
		FriendViewModel friendViewModel = new FriendViewModel(repository);
		friendViewModel.setFabCommand(fabCommand);
		friendViewModel.setOpenCamera(cameraCommand);
		return friendViewModel;
	}
}
