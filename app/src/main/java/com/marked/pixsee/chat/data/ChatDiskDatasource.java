package com.marked.pixsee.chat.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.mapper.CursorToMessageMapper;
import com.marked.pixsee.data.mapper.Mapper;
import com.marked.pixsee.data.mapper.MessageToCVMapper;
import com.marked.pixsee.friends.data.User;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 2016-05-20.
 */
public class ChatDiskDatasource implements ChatDatasource {
	private PixyDatabase database;

	public ChatDiskDatasource(PixyDatabase database) {
		this.database = database;
	}

	Mapper<Cursor, Message> cursorToMessageMapper;
	Mapper<Message, ContentValues> messageToCVMapper = new MessageToCVMapper();

	@Override
	public Observable<List<Message>> getMessages(User friend) {
		List<Message> users = new ArrayList<>();
		database.getReadableDatabase().beginTransaction();
		Cursor cursor = database.getReadableDatabase().query(MessageContract.TABLE_NAME, null, "_to=?",
				new String[]{friend.getUserID()}, null, null, null);
		cursorToMessageMapper = new CursorToMessageMapper(cursor);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Message message = cursorToMessageMapper.map(cursor);
			users.add(message);
			cursor.moveToNext();
		}
		database.getReadableDatabase().setTransactionSuccessful();
		database.getReadableDatabase().endTransaction();
		cursor.close();
		return Observable.just(users);
	}

	@Override
	public Observable<Message> getMessage(@NonNull Message messageId) {
		Cursor cursor = database.getReadableDatabase().query(MessageContract.TABLE_NAME, null,"_id=?",
				new String[]{messageId.getId()}, null, null, null);
		cursor.moveToFirst();
		return Observable.just(cursorToMessageMapper.map(cursor));
	}

	@Override
	public void updateMessage(@NonNull Message message) {
		database.getWritableDatabase().update(MessageContract.TABLE_NAME,
				messageToCVMapper.map(message),
				MessageContract._ID + " = ?",
				new String[]{message.getId()});
	}

	@Override
	public void saveMessage(@NonNull Message message) {
		database.getWritableDatabase()
				.insertWithOnConflict(MessageContract.TABLE_NAME,
						null,
						messageToCVMapper.map(message),
						SQLiteDatabase.CONFLICT_REPLACE);
	}

	@Override
	public void saveMessage(@NonNull List<Message> messages) {
		SQLiteDatabase writableDatabase = database.getWritableDatabase();
		writableDatabase.beginTransaction();
		for (Message message : messages)
			writableDatabase.insertWithOnConflict(MessageContract.TABLE_NAME, null, messageToCVMapper.map(message), SQLiteDatabase.CONFLICT_REPLACE);
		writableDatabase.setTransactionSuccessful();
		writableDatabase.endTransaction();
	}

	@Override
	public void refreshMessages() {

	}

	@Override
	public void deleteAllMessages() {

	}

	@Override
	public void deleteMessages(@NonNull Message messageId) {
		// FIXME: 6/2/2016 this get's called 2 times when chat bubble explodes, why ?
		database.getWritableDatabase().delete(MessageContract.TABLE_NAME, MessageContract.COLUMN_ID + " = ?",
				new String[]{messageId.getId()});
	}
}
