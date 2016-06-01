package com.marked.pixsee.chat;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.friends.data.User;
import com.marked.pixsee.service.GCMListenerService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Tudor on 2016-05-19.
 */
public interface ChatContract {

	interface Presenter extends BasePresenter, GCMListenerService.Callbacks{
		void loadMore(int limit);

		void loadMore(int limit, boolean forceUpdate);

		void loadMore(int limit, User user);

		void execute(Command command);

		void isTyping(boolean typing);

		void chatClicked(Message message);

		void sendMessage(@NotNull Message message);
	}

	interface View extends BaseView<Presenter> {
		void showCards(List<Message> cards);

		void showNoChats();

		void addMessage(Message message);

		void pop();
	}
}
