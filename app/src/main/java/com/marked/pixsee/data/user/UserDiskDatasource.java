package com.marked.pixsee.data.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.marked.pixsee.chat.data.MessageContract;
import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.friends.data.FriendContractDB;
import com.marked.pixsee.friends.mapper.CursorToUserMapper;
import com.marked.pixsee.friends.mapper.UserToCvMapper;
import com.marked.pixsee.friends.specifications.GetFriendsSpecification;
import com.marked.pixsee.friends.specifications.GetFriendsStartingWith;
import com.marked.pixsee.injection.scopes.ActivityScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

import static com.marked.pixsee.friends.data.FriendContractDB.TABLE_NAME;

/**
 * Created by Tudor on 2016-05-20.
 */
@ActivityScope
public class UserDiskDatasource implements UserDatasource {
	private SQLiteOpenHelper db;
	private Mapper<Cursor, User> cursorToUserMapper = new CursorToUserMapper();
	private Mapper<User, ContentValues> userToCvMapper = new UserToCvMapper();

	@Inject
	public UserDiskDatasource(SQLiteOpenHelper db) {
		this.db = db;
	}

	@Override
	public Observable<List<User>> getUsers() {
		List<User> users = new ArrayList<>();
		db.getReadableDatabase().beginTransaction();
		Cursor cursor = db.getReadableDatabase().rawQuery(new GetFriendsSpecification(0, -1).createQuery(), null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			User friend = cursorToUserMapper.map(cursor);
			users.add(friend);
			cursor.moveToNext();
		}
		db.getReadableDatabase().setTransactionSuccessful();
		db.getReadableDatabase().endTransaction();
		cursor.close();
		return Observable.just(users);
	}

	@Override
	public Observable<User> getUser(@NonNull User UserId) {
		Cursor cursor = db.getReadableDatabase().query(TABLE_NAME, null, UserId.getUserID()+"=?",
				new String[]{UserId.getUserID()}, null, null, null);
		cursor.moveToFirst();
		return Observable.just(cursorToUserMapper.map(cursor));
	}
	@Override
	public User getUser(@NonNull String tablename) {
		Cursor cursor = db.getReadableDatabase().query(tablename, null, null,null, null, null, null);
		cursor.moveToFirst();
		User user = cursorToUserMapper.map(cursor);
		cursor.close();
		return user;
	}

	@Override
	public Observable<JsonObject> saveUser(@NonNull User user) {
		db.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null,userToCvMapper.map(user),SQLiteDatabase.CONFLICT_REPLACE);
		return Observable.empty();
	}

	@Override
	public Observable saveAppUser(@NonNull User user) {
		db.getWritableDatabase().insertWithOnConflict(DatabaseContract.AppsUser.TABLE_NAME, null,userToCvMapper.map(user),SQLiteDatabase.CONFLICT_REPLACE);
		return Observable.empty();
	}

	@Override
	public Observable<List<User>> refreshUsers() {
		return Observable.empty();
	}

	@Override
	public void clear() {

	}

	@Override
	public void deleteAllUsers() {
		db.getWritableDatabase().delete(TABLE_NAME, null, null);
		db.getWritableDatabase().delete(DatabaseContract.AppsUser.TABLE_NAME, null, null);
		db.getWritableDatabase().delete(MessageContract.TABLE_NAME, null, null);
		db.close();
	}

	@Override
	public Observable<List<User>> getUsers(String byName) {
		List<User> users = new ArrayList<>();
		db.getReadableDatabase().beginTransaction();
		Cursor cursor = db.getReadableDatabase().rawQuery(new GetFriendsStartingWith(byName,0, -1).createQuery(), null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			User friend = cursorToUserMapper.map(cursor);
			users.add(friend);
			cursor.moveToNext();
		}
		db.getReadableDatabase().setTransactionSuccessful();
		db.getReadableDatabase().endTransaction();
		cursor.close();
		return Observable.just(users);
	}

	@Override
	public void deleteUsers(@NonNull User userId) {
		db.getWritableDatabase().delete(TABLE_NAME, FriendContractDB._ID + " = ?", new String[]{userId.getUserID()});
	}

	@Override
	public void saveUser(@NonNull List<User> users) {
		db.getWritableDatabase().beginTransaction();
		try {
			for (User user : users) {
				db.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null, userToCvMapper.map(user), SQLiteDatabase.CONFLICT_REPLACE);
			}
			db.getWritableDatabase().setTransactionSuccessful();
		} finally {
			db.getWritableDatabase().endTransaction();
		}
	}
}
