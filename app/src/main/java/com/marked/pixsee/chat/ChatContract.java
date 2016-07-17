package com.marked.pixsee.chat;

import android.net.Uri;
import android.os.Bundle;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.data.user.User;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * Created by Tudor on 2016-05-19.
 */
interface ChatContract {

	interface Presenter extends BasePresenter, GCMListenerService.Callback{
		void loadMore(int limit, boolean forceUpdate);

		void loadMore(int limit, User user);

		void execute(Command command);

		void isTyping(boolean typing);

		void chatClicked(Message message,int position);

		void sendMessage(@NotNull Message message);

		void sendImage(@NotNull Message message);

		void receiveMessage(String message, Bundle bundle);


		void setChatInteraction(ChatFragment.ChatFragmentInteraction listener);

		void pictureTaken(File file);

		void onCameraClick();

		void clearImageButton();

		File getPictureFile();

		void chatImageClicked(Uri uri);

		void setThatUser(User thatUser);
	}

	interface View extends BaseView<Presenter> {
		void showMessages(List<Message> cards);

		void showEmptyMessages();

		void addMessage(Message message);

		void pop();

		void remove(Message message, int position);

		void showImage(File path);

		void pictureTaken(File file);

		void showPictureContainer(boolean show);

		void showImageFullscreen(Uri uri);

		void imageSent(Message message);
	}
}
