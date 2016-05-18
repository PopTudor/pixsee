package com.marked.pixsee.friends;

import com.marked.pixsee.data.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Tudor Pop on 15-Mar-16.
 * This specifies the contract between the view and the presenter.
 */
public interface FriendsContract {
	interface Presenter {
		List<User> getFriends();

		List<User> getFriends(int start);

		List<User> getFriends(int start, int end);

		void loadMore(@NotNull int num);

		void clear();

	}
	interface View{

	}
}
