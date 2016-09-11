package com.marked.pixsee.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tudor Pop on 26-Jan-16.
 */
public class PixyDatabaseFake extends SQLiteOpenHelper {

	private PixyDatabaseFake(Context context) {
		super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
	}


	private static PixyDatabaseFake instance;

	public static PixyDatabaseFake getInstance(Context context) {
		if (instance == null)
			instance = new PixyDatabaseFake(context);
		return instance;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
}