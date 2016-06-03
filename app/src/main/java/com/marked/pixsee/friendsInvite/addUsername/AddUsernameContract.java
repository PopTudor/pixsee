package com.marked.pixsee.friendsInvite.addUsername;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.friends.data.User;

import java.util.List;

/**
 * Created by Tudor on 03-Jun-16.
 */
public interface AddUsernameContract {
	interface Presenter extends BasePresenter{
		void search(String usernameOrEmail);
	}

	interface View extends BaseView<Presenter> {
		void showUsers(List<User> users);
	}
}
