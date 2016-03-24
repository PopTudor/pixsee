package com.marked.pixsee.friends;

import com.marked.pixsee.data.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Tudor Pop on 15-Mar-16.
 * This specifies the contract between the view and the presenter.
 */
public interface FriendsContract {
	/*Friend views should implement View*/
	interface View {

		/**
		 * Open detail view for a {@link User}
		 *
		 * @param friend The {@link User} to show details of
		 */
		void showFriendDetailUI(User friend);

		void onFriendsLoaded(int from, int to);
	}

	/**
	 * Presenter will implement this to handle actions on the view
	 */
	interface UserActionsListener {
		void onClickCamera(android.view.View view);

		void onClickFab(android.view.View view);
	}

	interface Model {
		List<User> getFriends();

		List<User> getFriends(int start);

		List<User> getFriends(int start, int end);

		void loadMore(@NotNull int num);

		void clear();

	}
}
