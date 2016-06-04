package com.marked.pixsee.friendsInvite.addUsername.di;

import com.marked.pixsee.data.repository.user.UserRepository;
import com.marked.pixsee.friendsInvite.addUsername.AddUsernameContract;
import com.marked.pixsee.injection.scopes.PerFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 03-Jun-16.
 */
@Module
@PerFragment
public class AddUserModule {
	private AddUsernameContract.View view;

	public AddUserModule(AddUsernameContract.View view) {
		this.view = view;
	}

	@Provides
	@PerFragment
	AddUsernameContract.Presenter providesPresenter(UserRepository repository) {
		return new Presenter(view, repository);
	}
}
