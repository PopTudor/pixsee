package com.marked.pixsee.friends.data;

import android.provider.BaseColumns;

/**
 * Created by Tudor on 2016-05-27.
 */
public interface DatabaseFriendContract extends BaseColumns {
	String TABLE_NAME = "friends";
	String COLUMN_ID = FriendConstants.ID; /* id from online stored database */
	String COLUMN_NAME = FriendConstants.NAME;
	String COLUMN_EMAIL = FriendConstants.EMAIL;
	String COLUMN_TOKEN = FriendConstants.TOKEN;
	String[] ALL_TABLES = new String[]{DatabaseFriendContract.COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_TOKEN};
	String CREATE_TABLE =
			"CREATE TABLE " + TABLE_NAME
					+ "(" +
//					BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					COLUMN_ID + " TEXT PRIMARY KEY," +
					COLUMN_NAME + " TEXT," +
					COLUMN_EMAIL + " TEXT," +
					COLUMN_TOKEN + " TEXT "
					+ ") WITHOUT ROWID;";
	String DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME";
}
