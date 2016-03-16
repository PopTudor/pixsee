package com.marked.pixsee.friends;

import com.marked.pixsee.friends.data.Friend;

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
		 * Open detail view for a {@link Friend}
		 *
		 * @param friend The {@link Friend} to show details of
		 */
		void showFriendDetailUI(Friend friend);

		void onFriendsLoaded(int from, int to);
	}

	/**
	 * Presenter will implement this to handle actions on the view
	 */
	interface UserActionsListener {
		void loadFriends(int num);

		void loadFriends(boolean forceUpdate);

		void openFriendDetailUI(@NotNull Friend friend);
	}

	interface Model {
		List<Friend> getFriends();

		List<Friend> getFriends(int start);

		List<Friend> getFriends(int start, int end);

		void loadMore(@NotNull int num);

		void clear();

	}
}
