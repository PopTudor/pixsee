package com.marked.pixsee.chat;

import android.os.Bundle;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.chat.data.ChatDatasource;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.data.repository.user.User;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 2016-05-19.
 */
public class ChatPresenter implements ChatContract.Presenter {
	private WeakReference<ChatContract.View> mView;
	private ChatDatasource mRepository;
	private User mAppsUser;
	private boolean mShowTypingAnimation = true;
	private ChatFragment.ChatFragmentInteraction mChatFragmentInteraction;
	private File mPictureFile;

	public ChatPresenter(ChatContract.View mView, ChatDatasource mRepository,User appsUser) {
		this.mRepository = mRepository;
		this.mView = new WeakReference<>(mView);
		this.mView.get().setPresenter(this);
		mAppsUser = appsUser;
	}


	@Override
	public void attach() {
		loadMore(50, true);
	}

	@Override
	public void loadMore(int limit) {
		loadMore(limit, false);
	}

	@Override
	public void loadMore(int limit, User friendId) {
		mRepository.getMessages(friendId)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<Message>>() {
					@Override
					public void call(List<Message> messages) {
						mView.get().showCards(messages);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						mView.get().showNoChats();
					}
				});
	}

	@Override
	public void receiveRemoteMessage(RemoteMessage remoteMessage) {

	}

	@Override
	public void receiveMessage(String from, Bundle data) {
		Message message = new Message.Builder()
				.addData(data)
				.messageType(MessageConstants.MessageType.YOU_MESSAGE)
				.from(from)
				.to(from)
				.build();
		mRepository.saveMessage(message);
		mView.get().addMessage(message);
	}

	@Override
	public void sendMessage(@NotNull Message message) {
		mRepository.saveMessage(message);
		mView.get().addMessage(message);
	}

	@Override
	public File getPictureFile() {
		return mPictureFile;
	}

	@Override
	public void isTyping(boolean typing) {
		if (typing && mShowTypingAnimation) {
			mShowTypingAnimation = false;
			Message typingMessage = new Message.Builder()
					.messageType(MessageConstants.MessageType.TYPING)
					.build();
			mView.get().addMessage(typingMessage);
		}
		if (!typing) {
			mView.get().pop();
			mShowTypingAnimation = true;
		}
	}

	@Override
	public void chatClicked(Message message, int position) {
		mRepository.deleteMessages(message);
		mView.get().remove(message, position);
	}

	@Override
	public void filtersButtonClicked() {
		mView.get().showFilters();
	}

	public void setChatInteraction(ChatFragment.ChatFragmentInteraction listener) {
		mChatFragmentInteraction = listener;
	}

	@Override
	public void pictureTaken(File file) {
		mPictureFile = file;
		mView.get().showImage(file);
	}

	@Override
	public void onCameraClick() {
		mChatFragmentInteraction.onCameraClick();
	}

	@Override
	public void clearImageButton() {
		mView.get().showPictureContainer(false);
	}

	@Override
	public void loadMore(int limit, boolean forceUpdate) {

	}

	@Override
	public void execute(Command command) {
		command.execute();
	}
}
