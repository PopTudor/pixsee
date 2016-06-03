package com.marked.pixsee.friendsInvite.addUsername;

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
}
