package com.marked.pixsee.main;

import com.marked.pixsee.RxBus;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.main.strategy.ProfilePictureStrategy;
import com.marked.pixsee.main.strategy.ShareStrategy;

import java.lang.ref.WeakReference;

import rx.functions.Action1;

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
		RxBus.getInstance().register(FriendRequestEvent.class, new Action1<FriendRequestEvent>() {
			@Override
			public void call(FriendRequestEvent friendRequestEvent) {
				mWeakView.get().friendRequestEvent(friendRequestEvent);
			}
		});
	}

	@Override
	public void friendRequest(User user) {
		mWeakView.get().showFriendRequestDialog(user);
	}
}
