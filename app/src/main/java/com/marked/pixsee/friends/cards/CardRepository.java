package com.marked.pixsee.friends.cards;

import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.data.repository.Repository;
import com.marked.pixsee.data.repository.Specification;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor on 2016-05-19.
 */
public class CardRepository implements Repository<Message> {
	private PixyDatabase database;

	public CardRepository(PixyDatabase database) {
		this.database = database;
	}

//	Mapper<Cursor, Message> cursorToMessageMapper = new CursorToMessageMapper();
//	Mapper<Message, Cursor> messageToCursorMapper = new MessageToCursorMapper();

	@Override
	public void add(@NotNull Message item) {
//		database.getWritableDatabase().insert()
	}

	@Override
	public void add(@NotNull List<Message> items) {

	}

	@Override
	public void update(@NotNull Message item) {

	}

	@Override
	public void remove(@NotNull Message item) {

	}

	@Override
	public void remove(Specification specification) {

	}

	@Override
	public Observable<List<Message>> query(Specification specification) {
		return null;
	}
}
