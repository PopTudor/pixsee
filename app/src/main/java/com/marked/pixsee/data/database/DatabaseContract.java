package com.marked.pixsee.data.database;

import android.provider.BaseColumns;

import com.marked.pixsee.chat.data.MessageContract;
import com.marked.pixsee.friends.data.FriendContractDB;
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
	public static final String[] CREATE_TABLE_ARRAY = {FriendContractDB.CREATE_TABLE.trim(), AppsUser.CREATE_TABLE.trim(), MessageContract.CREATE_TABLE.trim()};
	public static final String[] DELETE_TABLE_ARRAY = {FriendContractDB.DELETE_TABLE, AppsUser.DELETE_TABLE, MessageContract.DELETE_TABLE};

	/* App's user*/
	public static class AppsUser implements BaseColumns {
		public static final String TABLE_NAME = "user";
		public static final String COLUMN_ID = FriendConstants.ID;
		public static final String COLUMN_NAME = FriendConstants.NAME;
		public static final String COLUMN_EMAIL = FriendConstants.EMAIL;
		public static final String COLUMN_COVER_URL = FriendConstants.COVER_URL;
		public static final String COLUMN_ICON_URL = FriendConstants.ICON_URL;
		public static final String COLUMN_USERNAME = FriendConstants.USERNAME;
		public static final String COLUMN_TOKEN = FriendConstants.TOKEN;
		public static final String COLUMN_PASSWORD = FriendConstants.PASSWORD;

		public static final String CREATE_TABLE =
				" CREATE TABLE " + TABLE_NAME
						+ "("
						+ COLUMN_ID + " TEXT PRIMARY KEY,"
						+ COLUMN_NAME + " TEXT,"
						+ COLUMN_PASSWORD + " TEXT,"
						+ COLUMN_EMAIL + " TEXT,"
						+ COLUMN_USERNAME + " TEXT,"
						+ COLUMN_COVER_URL + " TEXT,"
						+ COLUMN_ICON_URL + " TEXT,"
						+ COLUMN_TOKEN + " TEXT"
						+ ") WITHOUT ROWID;";
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME";
	}

}