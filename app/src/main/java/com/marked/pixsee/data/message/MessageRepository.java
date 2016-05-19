package com.marked.pixsee.data.message;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.mapper.CursorToMessageMapper;
import com.marked.pixsee.data.mapper.Mapper;
import com.marked.pixsee.data.mapper.MessageToCVMapper;
import com.marked.pixsee.data.repository.Repository;
import com.marked.pixsee.data.repository.SQLSpecification;
import com.marked.pixsee.data.repository.Specification;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 2016-05-19.
 */
public class MessageRepository implements Repository<Message> {
	private PixyDatabase database;

	public MessageRepository(PixyDatabase database) {
		this.database = database;
	}

	Mapper<Cursor, Message> cursorToMessageMapper = new CursorToMessageMapper();
	Mapper<Message, ContentValues> messageToCVMapper = new MessageToCVMapper();

	@Override
	public void add(@NotNull Message item) {
		database.getWritableDatabase().insert(DatabaseContract.Message.TABLE_NAME, null, messageToCVMapper.map(item));
	}

	@Override
	public void add(@NotNull List<Message> items) {
		SQLiteDatabase writableDatabase = database.getWritableDatabase();
		writableDatabase.beginTransaction();
		for (Message message:items)
			writableDatabase.insertWithOnConflict(DatabaseContract.Message.TABLE_NAME, null, messageToCVMapper.map(message), SQLiteDatabase.CONFLICT_REPLACE);
		writableDatabase.endTransaction();
		writableDatabase.setTransactionSuccessful();
	}

	@Override
	public void update(@NotNull Message item) {
		database.getWritableDatabase().update(DatabaseContract.Message.TABLE_NAME,
				messageToCVMapper.map(item),
				DatabaseContract.Message._ID + " = ?",
				new String[]{item.getId()});
	}

	@Override
	public void remove(@NotNull Message item) {
		database.getWritableDatabase().delete(DatabaseContract.Message.TABLE_NAME, DatabaseContract.Message._ID + " = ?",
				new String[]{item.getId()});
	}

	@Override
	public void remove(Specification specification) {
		database.getWritableDatabase().execSQL(((SQLSpecification)specification).createQuery());
	}

	@Override
	public Observable<List<Message>> query(Specification specification) {
		return null;
	}
}
