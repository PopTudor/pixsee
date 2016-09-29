package com.marked.pixsee.main;

import android.support.annotation.NonNull;

import com.marked.pixsee.main.strategy.ProfilePictureStrategy;
import com.marked.pixsee.main.strategy.ShareStrategy;
import com.marked.pixsee.model.database.DatabaseContract;
import com.marked.pixsee.model.user.User;
import com.marked.pixsee.model.user.UserDatasource;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-27.
 */
class MainPresenter implements MainContract.Presenter {
	private WeakReference<MainContract.View> mWeakView;
	private UserDatasource mRepository;

	public MainPresenter(MainContract.View view, UserDatasource userDatasource) {
		this.mRepository = userDatasource;
		this.mWeakView = new WeakReference<>(view);
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
		User user = mRepository.getUser(DatabaseContract.AppsUser.TABLE_NAME);
		mWeakView.get().showProfile(user);
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
	public void friendRequest(@NonNull User user) {
		mWeakView.get().showFriendRequestDialog(user);
	}
}
