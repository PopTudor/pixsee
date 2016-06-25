package com.marked.pixsee.friendsInvite.addUsername.di;

import android.content.Context;

import com.marked.pixsee.R;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.data.repository.user.UserDatasource;
import com.marked.pixsee.friendsInvite.addUsername.AddUsernameContract;
import com.marked.pixsee.injection.Repository;
import com.marked.pixsee.injection.scopes.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 03-Jun-16.
 */
@Module
@FragmentScope
public class AddUserModule {
	private AddUsernameContract.View view;

	public AddUserModule(AddUsernameContract.View view) {
		this.view = view;
	}

	@Provides
	@FragmentScope
	AddUsernameContract.Presenter providesPresenter(@Repository UserDatasource repository, Context context) {
		User user = repository.getUser(DatabaseContract.AppsUser.TABLE_NAME);

		return new Presenter(view, repository,new NetworkService(user,context.getString(R.string.gcm_defaultSenderId)));
	}
}
