package com.marked.pixsee.data.database;

import android.provider.BaseColumns;

import com.marked.pixsee.chat.data.MessageContract;
import com.marked.pixsee.friends.data.DatabaseFriendContract;
import com.marked.pixsee.friends.data.FriendConstants;

/**
 * Created by Tudor Pop on 26-Jan-16.
 */
public class DatabaseContract {

	private DatabaseContract() {
	}

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "pixy.db";

	/* Add to CREATE_TABLE_ARRAY all the other tables that get created and to DELETE_TABLE_ARRAY all tables that get deleted*/
	public static final String[] CREATE_TABLE_ARRAY = {DatabaseFriendContract.CREATE_TABLE.trim(), User.CREATE_TABLE.trim(), MessageContract.CREATE_TABLE.trim()};
	public static final String[] DELETE_TABLE_ARRAY = {DatabaseFriendContract.DELETE_TABLE, User.DELETE_TABLE, MessageContract.DELETE_TABLE};

	/* App's user*/
	protected static class User implements BaseColumns {
		public static final String TABLE_NAME = "user";
		public static final String COLUMN_ID = FriendConstants.ID;
		public static final String COLUMN_NAME = FriendConstants.NAME;
		public static final String COLUMN_EMAIL = FriendConstants.EMAIL;
		public static final String COLUMN_TOKEN = FriendConstants.TOKEN;

		public static final String CREATE_TABLE =
				" CREATE TABLE " + TABLE_NAME
						+ "(" +
						BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ COLUMN_ID + " TEXT,"
						+ COLUMN_NAME + " TEXT,"
						+ COLUMN_EMAIL + " TEXT,"
						+ COLUMN_TOKEN + " TEXT"
						+ ");";
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME";
	}

}