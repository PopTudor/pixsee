package com.marked.pixsee.features.friendsInvite;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.commands.Command;

/**
 * Created by Tudor on 03-Jun-16.
 */
public interface FriendsInviteContract {
	interface Presenter extends BasePresenter,Command{
		void addFriendClicked();

		void shareUsernameClicked();
	}
	interface View extends BaseView<Presenter>{

		void showAddFriend();

		void showUsernameDirectShare();
	}
}
