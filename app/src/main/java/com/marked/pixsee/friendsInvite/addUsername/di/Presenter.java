package com.marked.pixsee.friendsInvite.addUsername.di;

import com.marked.pixsee.friends.data.User;
import com.marked.pixsee.friendsInvite.addUsername.AddUsernameContract;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 03-Jun-16.
 */
class Presenter implements AddUsernameContract.Presenter {
	private WeakReference<AddUsernameContract.View> mView;

	public Presenter(AddUsernameContract.View view) {
		mView = new WeakReference<AddUsernameContract.View>(view);
		mView.get().setPresenter(this);
	}

	@Override
	public void start() {

	}

	@Override
	public void search(String usernameOrEmail) {

	}

	@Override
	public void onClick(User user, int position) {

	}
}
