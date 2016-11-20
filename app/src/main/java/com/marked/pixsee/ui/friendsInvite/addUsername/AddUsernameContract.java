package com.marked.pixsee.ui.friendsInvite.addUsername;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;

import java.util.List;

/**
 * Created by Tudor on 03-Jun-16.
 */
interface AddUsernameContract {
	interface Presenter extends BasePresenter, UsersAdapter.UserInteraction {
		void search(String usernameOrEmail);

		@Override
		void onClick(Relationship relationship, int position);
	}

	interface View extends BaseView<Presenter> {
		void showUsers(List<Relationship> users);

		void showNoInternetConnection();
	}
}
