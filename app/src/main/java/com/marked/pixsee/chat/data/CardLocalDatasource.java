package com.marked.pixsee.chat.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.mapper.CursorToMessageMapper;
import com.marked.pixsee.data.mapper.Mapper;
import com.marked.pixsee.data.mapper.MessageToCVMapper;
import com.marked.pixsee.friends.data.DatabaseFriendContract;
import com.marked.pixsee.friends.specifications.GetMessagesByGroupedByDate;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.marked.pixsee.friends.data.DatabaseFriendContract.TABLE_NAME;

/**
 * Created by Tudor on 2016-05-20.
 */
public class CardLocalDatasource implements CardDatasource {
	private PixyDatabase database;

	public CardLocalDatasource(PixyDatabase database) {
		this.database = database;
	}

	Mapper<Cursor, Message> cursorToMessageMapper = new CursorToMessageMapper();
	Mapper<Message, ContentValues> messageToCVMapper = new MessageToCVMapper();

	@Override
	public Observable<List<Message>> getMessagesOfFriend(String friendId) {
		List<Message> users = new ArrayList<>();
		database.getReadableDatabase().beginTransaction();
		Cursor cursor = database.getReadableDatabase().rawQuery(new GetMessagesByGroupedByDate(friendId).createQuery(), null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Message friend = cursorToMessageMapper.map(cursor);
			users.add(friend);
			cursor.moveToNext();
		}
		database.getReadableDatabase().setTransactionSuccessful();
		database.getReadableDatabase().endTransaction();
		cursor.close();
		return Observable.just(users);
	}

	@Override
	public Observable<Message> getMessage(@NonNull Message messageId) {
		Cursor cursor = database.getReadableDatabase().query(TABLE_NAME, DatabaseFriendContract.ALL_TABLES, "WHERE "+messageId.getId()+"=?",
				new String[]{messageId.getId()}, null, null, null);
		cursor.moveToFirst();
		return Observable.just(cursorToMessageMapper.map(cursor));
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
