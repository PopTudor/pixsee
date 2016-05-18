package com.marked.pixsee.friends.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.marked.pixsee.data.User;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.mapper.CursorToUserMapper;
import com.marked.pixsee.data.mapper.Mapper;
import com.marked.pixsee.data.mapper.UserToCvMapper;
import com.marked.pixsee.data.repository.Repository;
import com.marked.pixsee.data.repository.SQLSpecification;
import com.marked.pixsee.data.repository.Specification;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.marked.pixsee.data.database.DatabaseContract.Friend.TABLE_NAME;


/**
 * Created by Tudor Pop on 12-Dec-15.
 * Singleton class used to keep all the friends of the user
 */
public class FriendRepository implements Repository<User> {
	private PixyDatabase db;
	public FriendRepository(PixyDatabase db) {
		this.db = db;
	}

	private Mapper<Cursor, User> cursorToUserMapper = new CursorToUserMapper();
	private Mapper<User, ContentValues> userToCvMapper = new UserToCvMapper();

	public int length() {
		int size;
		Cursor cursor = db.getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, null);
		size = cursor.getCount();
		cursor.close();
		return size;
	}

	@Override
	public void update(@NotNull User item) {
		db.getWritableDatabase().update(TABLE_NAME, userToCvMapper.map(item), DatabaseContract.Friend.COLUMN_ID + " = ?", new String[]{item.getUserID()});
	}


	@Override
	public Observable<List<User>> query(Specification specification) {
		List<User> users = new ArrayList<>();

		if (specification instanceof SQLSpecification) {
			db.getReadableDatabase().beginTransaction();
			Cursor cursor = db.getReadableDatabase().rawQuery(((SQLSpecification) specification).createQuery(), null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				User friend = cursorToUserMapper.map(cursor);
				users.add(friend);
				cursor.moveToNext();
			}
			db.getReadableDatabase().setTransactionSuccessful();
			db.getReadableDatabase().endTransaction();
			cursor.close();
		}

		return Observable.just(users);
	}

	@Override
	public void add(@NonNull User element) {
		db.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null, userToCvMapper.map(element), SQLiteDatabase.CONFLICT_IGNORE);
	}

	@Override
	public void add(@NonNull List<User> elements) {
		db.getWritableDatabase().beginTransaction();
		{
			for (User element : elements) {
				db.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null, userToCvMapper.map(element), SQLiteDatabase.CONFLICT_IGNORE);
			}
		}
		db.getWritableDatabase().setTransactionSuccessful();
	}

	@Override
	public void remove(@NonNull User element) {
		db.getWritableDatabase().delete(TABLE_NAME, DatabaseContract.Friend.COLUMN_ID + " = ?", new String[]{element.getUserID()});
	}

	@Override
	public void remove(Specification specification) {

	}
//	fun JSONArray.contactListfromJSONArray(startingIndex:Int=0):List<User>{
//			                                                                      val contacts=ArrayList<User>()
//
//					                                                                                                  var result:JSONObject
//					                                                                                                  var id:String
//					                                                                                                  var name:String
//					                                                                                                  var email:String
//					                                                                                                  var token:String
//					                                                                                                  for(i in startingIndex..length()-1){
//					                                                                                                  result=getJSONObject(i)
//					                                                                                                  id=result.getString(FriendConstants.ID)
//					                                                                                                  name=result.getString(FriendConstants.NAME)
//					                                                                                                  email=result.getString(FriendConstants.EMAIL)
//					                                                                                                  token=result.getString(FriendConstants.TOKEN)
//
//					                                                                                                  contacts.add(User(id,name,email,token))
//					                                                                                                  }
//
//					                                                                                                  return contacts
//					                                                                                                  }
}

