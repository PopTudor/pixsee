package com.marked.pixsee.main;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.friendsInvite.addUsername.data.RequestService;

/**
 * Created by Tudor on 2016-05-27.
 */
public interface MainContract {
	interface Presenter extends BasePresenter,RequestService {
		void execute(Command command);

		void chatClicked();

		void profileClicked();

		void friendRequest(User user, boolean b);

		void cameraClicked(int requestCode);
	}

	interface View extends BaseView<Presenter> {
		void showChat(boolean show);

		void showFriendRequestDialog(User user);

		void showProfile(User user);

		void showCamera(int requestCode);
	}
}
