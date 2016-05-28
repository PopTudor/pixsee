package com.marked.pixsee.chat;

import android.os.Bundle;

import com.marked.pixsee.chat.data.ChatDatasource;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.friends.data.User;

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
	private boolean mShowTypingAnimation =true;

	public ChatPresenter(ChatContract.View mView, ChatDatasource mRepository) {
		this.mRepository = mRepository;
		this.mView = new WeakReference<>(mView);
		this.mView.get().setPresenter(this);
	}

	@Override
	public void start() {
		loadMore(50, true);
	}

	@Override
	public void loadMore(int limit) {
//		loadMore(limit, false);
	}

	@Override
	public void loadMore(int limit, User friendId) {
		mRepository.getMessagesOfFriend(friendId)
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
	public void receiveMessage(String from, Bundle data) {
		int messageType = data.getInt("type", MessageConstants.MessageType.YOU_MESSAGE);
		Message message = new Message.Builder()
				                  .addData(data)
				                  .messageType(messageType)
				                  .from(from)
				                  .to(from)
				                  .build();
		mRepository.saveMessage(message);
		mView.get().addMessage(message);
	}

	@Override
	public void chatClicked(Message message) {

	}

	@Override
	public void isTyping(boolean typing) {
		if (typing && mShowTypingAnimation){
			mShowTypingAnimation = false;
			Message typingMessage = new Message.Builder()
					                        .messageType(MessageConstants.MessageType.TYPING)
					                        .build();
			mView.get().addMessage(typingMessage);
		}
		if(!typing) {
			mView.get().pop();
			mShowTypingAnimation = true;
		}
	}

	@Override
	public void loadMore(int limit, boolean forceUpdate) {

	}

	@Override
	public void execute(Command command) {
		command.execute();
	}
}
