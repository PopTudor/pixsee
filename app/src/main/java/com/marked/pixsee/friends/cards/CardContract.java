package com.marked.pixsee.friends.cards;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.data.message.Message;

import java.util.List;

/**
 * Created by Tudor on 2016-05-19.
 */
public interface CardContract {

	interface Presenter extends BasePresenter, CardAdapter.MessageInteraction {
		void loadMore(int limit);

		void loadMore(int limit, boolean forceUpdate);

		void loadMore(int limit, String text);

		void execute(Command command);

		@Override
		void start();

		@Override
		void messageClicked(Message message);

		@Override
		void moreClicked(Message message);

		@Override
		void favoriteClicked(Message message);

		@Override
		void replyClicked(Message message);
	}

	interface View extends BaseView<Presenter> {
		void showCards(List<Message> cards);

		void showNoCards();
	}
}
