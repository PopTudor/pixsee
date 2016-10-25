package com.marked.pixsee.data.chat;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.ui.chat.data.ChatDiskDatasource;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 25-Oct-16.
 */

public class FakeChatDiskDatasource extends ChatDiskDatasource {

	public FakeChatDiskDatasource(SQLiteOpenHelper database) {
		super(database);
	}

	@Override
	public Observable<List<Message>> getMessages(User friend) {
		return getRandomMessages();
	}

	private Observable<List<Message>> getRandomMessages() {
		return Observable.just(MessageTestUtils.getRandomMessageList(10));
	}

	@Override
	public Observable<Message> getMessage(@NonNull Message messageId) {
		return Observable.just(MessageTestUtils.getRandomMessage());
	}

	@Override
	public void updateMessage(@NonNull Message message) {
	}

	@Override
	public void saveMessage(@NonNull Message message) {
	}

	@Override
	public void saveMessage(@NonNull List<Message> messages) {
	}

	@Override
	public void refreshMessages() {
	}

	@Override
	public void deleteAllMessages() {
	}

	@Override
	public void deleteMessages(@NonNull Message messageId) {
	}
}
