package com.marked.pixsee.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tudor Pop on 26-Jan-16.
 */
public class PixyDatabase extends SQLiteOpenHelper {

	private static PixyDatabase instance;


	private PixyDatabase(Context context) {
		super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
	}

	public static PixyDatabase getInstance(Context context) {
		if (instance == null)
			instance = new PixyDatabase(context);
		return instance;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		for (String it : DatabaseContract.CREATE_TABLE_ARRAY)
			db.execSQL(it);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (String it : DatabaseContract.DELETE_TABLE_ARRAY)
			db.execSQL(it);
		onCreate(db);
	}
}