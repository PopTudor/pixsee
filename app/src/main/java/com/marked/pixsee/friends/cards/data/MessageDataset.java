package com.marked.pixsee.friends.cards.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.marked.pixsee.friends.friends.data.User;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.mapper.MessageToCVMapper;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Tudor Pop on 14-Feb-16.
 */
public class MessageDataset extends ArrayList<Message> {
	private Context mContext;
	private MessageToCVMapper messageToCVMapper = new MessageToCVMapper();

	public MessageDataset(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public boolean add(Message element) {
		if (element.getMessageType() != MessageConstants.MessageType.TYPING) /* if the message is a 'typing' message, then don't add it to the database */
			PixyDatabase.getInstance(mContext)
					.getWritableDatabase()
					.insertWithOnConflict(DatabaseContract.Message.TABLE_NAME, null, messageToCVMapper.map(element), SQLiteDatabase.CONFLICT_IGNORE);
		return super.add(element);
	}

	@Override
	public boolean addAll(Collection<? extends Message> collection) {
		boolean result = super.addAll(collection);
		{
			SQLiteDatabase database = PixyDatabase.getInstance(mContext).getWritableDatabase();
			database.beginTransaction();
			for (Message message : collection)
				database.insertWithOnConflict(DatabaseContract.Message.TABLE_NAME, null, messageToCVMapper.map(message), SQLiteDatabase.CONFLICT_IGNORE);
			database.endTransaction();
			database.setTransactionSuccessful();
		}
		return result;
	}

	@Override
	public Message set(int index, Message element) {
		PixyDatabase.getInstance(mContext)
				.getWritableDatabase()
				.update(DatabaseContract.Message.TABLE_NAME, messageToCVMapper.map(element), DatabaseContract.Message.COLUMN_ID + " = ?",
						new String[]{element.getId()});
		return super.set(index, element);
	}

	@Override
	public boolean remove(Object element) {
		Message message = (Message) element;
		PixyDatabase.getInstance(mContext).getWritableDatabase().
				                                                        delete(DatabaseContract.Message.TABLE_NAME, DatabaseContract.Message.COLUMN_ID + " = ?s", new String[]{message.getId()});
		return super.remove(element);
	}

	public static MessageDataset messages;

	public static MessageDataset getInstance(Context context) {
		if (messages == null)
			return new MessageDataset(context);
		return messages;
	}

	public JSONArray MessageListToJSONArray(List<Message> list) {
		JSONArray jsonArray = new JSONArray();
		for (Message it : list)
			jsonArray.put(it.toJSON());
		return jsonArray;
	}

	public void loadMore(User friend, int limit) {
		PixyDatabase.getInstance(mContext).getReadableDatabase().beginTransaction();
		Cursor cursor = PixyDatabase.getInstance(mContext).getReadableDatabase().query(
				DatabaseContract.Message.TABLE_NAME,
				new String[]{DatabaseContract.Message.COLUMN_DATA_BODY,
						DatabaseContract.Message.COLUMN_TYPE,
						DatabaseContract.Message.COLUMN_DATE,
						DatabaseContract.Message.COLUMN_TO},
				DatabaseContract.Message.COLUMN_TO + " = " + friend.getUserID(),
				null, null, null, null, String.valueOf(limit)
		);
		cursor.moveToFirst();
		String bodyColName = cursor.getColumnName(cursor.getColumnIndex(DatabaseContract.Message.COLUMN_DATA_BODY));
		String typeColName = cursor.getColumnName(cursor.getColumnIndex(DatabaseContract.Message.COLUMN_TYPE));
		String dateColName = cursor.getColumnName(cursor.getColumnIndex(DatabaseContract.Message.COLUMN_DATE));
		String toColName = cursor.getColumnName(cursor.getColumnIndex(DatabaseContract.Message.COLUMN_TO));
		while (!cursor.isAfterLast()) {
			Message message = new Message.Builder()
					                  .addData(MessageConstants.DATA_BODY, bodyColName)
					                  .messageType(Integer.parseInt(typeColName))
					                  .date(Long.parseLong(dateColName))
					                  .to(toColName).build();
			super.add(message);
		}
	}
}