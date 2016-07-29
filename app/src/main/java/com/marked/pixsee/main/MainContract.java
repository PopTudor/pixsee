package com.marked.pixsee.main;

import android.support.v7.app.AlertDialog;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.main.strategy.PictureActionStrategy;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Tudor on 2016-05-27.
 */
interface MainContract {
	interface Presenter extends BasePresenter {
		void chatTabClicked();

		void profileTabClicked();

		void profileImageClicked();

		void friendRequest(@NotNull User user, boolean b);

		void friendRequest(@NotNull User user);

		void cameraTabClicked();
	}

	interface View extends BaseView<Presenter> {
		void showChat(boolean show);

		AlertDialog showFriendRequestDialog(@NotNull User user);

		void showProfile(@NotNull User user);

		void showCamera(@NotNull PictureActionStrategy actionStrategy);

		void hideBottomNavigation();
	}
}
