package com.marked.pixsee.friends;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.data.user.User;

import java.util.List;

/**
 * Created by Tudor Pop on 15-Mar-16.
 * This specifies the contract between the view and the presenter.
 */
public interface FriendsContract {
	interface Presenter extends BasePresenter {

		void loadMore(final int limit,boolean forceUpdate);

		void execute(Command command);

		void loadMore(int num);

		void loadMore(final int limit,String text);

		void clear();

		void actionInviteClick();
	}

	interface View extends BaseView<Presenter> {
		void setRecyclerViewVisibility(int viewVisibility);

		void onFriendsInsert(List<User> list, int from, int to);

		void onFriendsReplace(List<User> list);

		void showInviteFriends();

		void showNoFriends();

		void showNoInternetConnection();

		void hideLoading();
	}
}
