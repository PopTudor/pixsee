package com.marked.pixsee.features.main;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.features.main.strategy.PictureActionStrategy;

/**
 * Created by Tudor on 2016-05-27.
 */
interface MainContract {
	interface Presenter extends BasePresenter {
		void chatTabClicked();

		void profileTabClicked();

		void profileImageClicked();

		void friendRequest(@NonNull User user, boolean b);

		void friendRequest(@NonNull User user);

		void cameraTabClicked();
	}

	interface View extends BaseView<Presenter> {
		void showChat(boolean show);

		AlertDialog showFriendRequestDialog(@NonNull User user);

		void showProfile(@NonNull User user);

		void showCamera(@NonNull PictureActionStrategy actionStrategy);

		void hideBottomNavigation();
	}
}
