package com.marked.pixsee.ui.friendsInvite;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 03-Jun-16.
 */
class Presenter implements FriendsInviteContract.Presenter {
	private WeakReference<FriendsInviteContract.View> mView;

	public Presenter(FriendsInviteContract.View mView) {
		this.mView = new WeakReference<FriendsInviteContract.View>(mView);
		mView.setPresenter(this);
	}

	@Override
	public void attach() {

	}

	@Override
	public void detach() {

	}

	@Override
	public void addFriendClicked() {
		mView.get().showAddFriend();
	}

	@Override
	public void execute() {

	}

	@Override
	public void shareUsernameClicked() {
		mView.get().showUsernameDirectShare();
	}
}
