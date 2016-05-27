package com.marked.pixsee.friends;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.friends.data.User;

import java.util.List;

/**
 * Created by Tudor Pop on 15-Mar-16.
 * This specifies the contract between the view and the presenter.
 */
public interface FriendsContract {
	interface Presenter extends BasePresenter {
		List<User> getFriends();

		void loadMore(boolean forceUpdate, final int limit);

		void execute(Command command);

		void loadMore( int num);

		void loadMore(String text, int limit);

		void clear();


	}

	interface View extends BaseView<Presenter> {
		void setRecyclerViewVisibility(int viewVisibility);

		void onFriendsInsert(List<User> list, int from, int to);

		void onFriendsReplace(List<User> list);

		/**
		 * Open detail view for a [User]
		 *
		 * @param friend The [User] to show details of
		 */
		void showFriendDetailUI(User friend);
		void showNoFriends();
	}
}
