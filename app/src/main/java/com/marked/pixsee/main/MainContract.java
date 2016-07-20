package com.marked.pixsee.main;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.friendsInvite.addUsername.data.RequestService;
import com.marked.pixsee.main.strategy.PictureActionStrategy;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Tudor on 2016-05-27.
 */
interface MainContract {
	interface Presenter extends BasePresenter,RequestService {
		void execute(Command command);

		void chatTabClicked();

		void profileTabClicked();

		void profileImageClicked();

		void friendRequest(User user, boolean b);

		void cameraTabClicked();
	}

	interface View extends BaseView<Presenter> {
		void showChat(boolean show);

		void showFriendRequestDialog(User user);

		void showProfile(User user);

		void showCamera(@NotNull PictureActionStrategy actionStrategy);

		void hideBottomNavigation();
	}
}
