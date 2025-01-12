package com.marked.pixsee.ui.main;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.ui.main.strategy.ProfilePictureStrategy;
import com.marked.pixsee.ui.main.strategy.ShareStrategy;

import java.lang.ref.WeakReference;

import rx.functions.Action1;

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
	public void friendRequest(@NonNull User user, boolean accepted) {
		if (accepted){
			mRepository.acceptFriendRequest(user)
					.subscribe(new Action1<JsonObject>() {
						@Override
						public void call(JsonObject jsonObject) {
							refreshFriendList();
						}
					}, new Action1<Throwable>() {
						@Override
						public void call(Throwable throwable) {
							throwable.printStackTrace();
						}
					});
		} else {
			mRepository.rejectFriendRequest(user)
					.subscribe(new Action1<JsonObject>() {
						@Override
						public void call(JsonObject jsonObject) {

						}
					}, new Action1<Throwable>() {
						@Override
						public void call(Throwable throwable) {
							throwable.printStackTrace();
						}
					});
		}
	}

	private void refreshFriendList() {
		mWeakView.get().refreshFriendList();
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
