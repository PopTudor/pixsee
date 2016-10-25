package com.marked.pixsee.ui.main;

import android.support.annotation.NonNull;

import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.ui.main.strategy.ProfilePictureStrategy;
import com.marked.pixsee.ui.main.strategy.ShareStrategy;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-27.
 */
class MainPresenter implements MainContract.Presenter {
	private WeakReference<MainContract.View> mWeakView;
	private UserDatasource mRepository;
	private UserManager mUserManager;

	MainPresenter(MainContract.View view, UserDatasource userDatasource, UserManager userManager) {
		this.mRepository = userDatasource;
		this.mWeakView = new WeakReference<>(view);
		mUserManager = userManager;
		this.mWeakView.get().setPresenter(this);
	}

	@Override
	public void friendRequest(User user, boolean accepted) {
		if (accepted){
			mRepository.saveUser(user)
			.subscribe();
		} else {

		}
	}

	@Override
	public void chatTabClicked() {
		mWeakView.get().showChat(true);
	}

	@Override
	public void profileTabClicked() {
		mWeakView.get().showProfile(mUserManager.getAppUser());
	}

	@Override
	public void cameraTabClicked() {
		mWeakView.get().hideBottomNavigation();
		mWeakView.get().showCamera(new ShareStrategy());
	}

	@Override
	public void profileImageClicked() {
		mWeakView.get().hideBottomNavigation();
		mWeakView.get().showCamera(new ProfilePictureStrategy());
	}

	@Override
	public void attach() {
		chatTabClicked();
	}

	@Override
	public void detach() {

	}

	@Override
	public void friendRequest(@NonNull User user) {
		mWeakView.get().showFriendRequestDialog(user);
	}
}
