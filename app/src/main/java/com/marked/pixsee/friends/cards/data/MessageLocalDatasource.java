package com.marked.pixsee.friends.cards.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.mapper.CursorToMessageMapper;
import com.marked.pixsee.data.mapper.Mapper;
import com.marked.pixsee.data.mapper.MessageToCVMapper;
import com.marked.pixsee.data.message.Message;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 2016-05-20.
 */
public class MessageLocalDatasource implements MessageDatasource {
	private PixyDatabase database;

	public MessageLocalDatasource(PixyDatabase database) {
		this.database = database;
	}

	Mapper<Cursor, Message> cursorToMessageMapper = new CursorToMessageMapper();
	Mapper<Message, ContentValues> messageToCVMapper = new MessageToCVMapper();

	@Override
	public Observable<List<Message>> getMessages() {
		return null;
	}

	@Override
	public Observable<Message> getMessage(@NonNull Message messageId) {
		return null;
	}

	@Override
	public void updateMessage(@NonNull Message message) {
		database.getWritableDatabase().update(DatabaseContract.Message.TABLE_NAME,
				messageToCVMapper.map(message),
				DatabaseContract.Message._ID + " = ?",
				new String[]{message.getId()});
	}

	@Override
	public void saveMessage(@NonNull Message message) {
		database.getWritableDatabase()
				.insertWithOnConflict(DatabaseContract.Message.TABLE_NAME,
						null,
						messageToCVMapper.map(message),
						SQLiteDatabase.CONFLICT_REPLACE);
	}

	@Override
	public void saveMessage(@NonNull List<Message> messages) {
		SQLiteDatabase writableDatabase = database.getWritableDatabase();
		writableDatabase.beginTransaction();
		for (Message message : messages)
			writableDatabase.insertWithOnConflict(DatabaseContract.Message.TABLE_NAME, null, messageToCVMapper.map(message), SQLiteDatabase.CONFLICT_REPLACE);
		writableDatabase.endTransaction();
		writableDatabase.setTransactionSuccessful();
	}

	@Override
	public void refreshMessages() {

	}

	@Override
	public void deleteAllMessages() {

	}

	@Override
	public void deleteMessages(@NonNull Message messageId) {
		database.getWritableDatabase().delete(DatabaseContract.Message.TABLE_NAME, DatabaseContract.Message._ID + " = ?",
				new String[]{messageId.getId()});
	}
}
