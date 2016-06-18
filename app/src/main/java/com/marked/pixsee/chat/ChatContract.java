package com.marked.pixsee.chat;

import android.os.Bundle;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.selfie.SelfieFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Tudor on 2016-05-19.
 */
public interface ChatContract {

	interface Presenter extends BasePresenter, GCMListenerService.Callback,SelfieFragment.SelfieTakePicture {
		void loadMore(int limit);

		void loadMore(int limit, boolean forceUpdate);

		void loadMore(int limit, User user);

		void execute(Command command);

		void isTyping(boolean typing);

		void chatClicked(Message message,int position);

		void sendMessage(@NotNull Message message);

		void receiveMessage(String message, Bundle bundle);

		void filtersButtonClicked();

		void setSelfieTakePicture(SelfieFragment.SelfieTakePicture listener);

	}

	interface View extends BaseView<Presenter> {
		void showCards(List<Message> cards);

		void showNoChats();

		void addMessage(Message message);

		void pop();

		void remove(Message message, int position);

		void showFilters();
	}
}
