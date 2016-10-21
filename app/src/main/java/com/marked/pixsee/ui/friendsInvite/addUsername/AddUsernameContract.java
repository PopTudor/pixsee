package com.marked.pixsee.ui.friendsInvite.addUsername;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.data.user.User;

import java.util.List;

/**
 * Created by Tudor on 03-Jun-16.
 */
public interface AddUsernameContract {
	interface Presenter extends BasePresenter, UsersAdapter.UserInteraction {
		void search(String usernameOrEmail);

		@Override
		void onClick(User user, int position);
	}

	interface View extends BaseView<Presenter> {
		void showUsers(List<User> users);

		void showNoInternetConnection();
	}
}
