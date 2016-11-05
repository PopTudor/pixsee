package com.marked.pixsee.data.friends;

import android.provider.BaseColumns;

/**
 * Created by Tudor on 2016-05-27.
 */
public interface FriendContractDB extends BaseColumns {
	String TABLE_NAME = "friends";
	String COLUMN_ID = FriendConstants.ID; /* id from online stored database */
	String COLUMN_NAME = FriendConstants.NAME;
	String COLUMN_PASSWORD = FriendConstants.PASSWORD;
	String COLUMN_EMAIL = FriendConstants.EMAIL;
	String COLUMN_TOKEN = FriendConstants.TOKEN;
	String COLUMN_COVER_URL = FriendConstants.COVER_URL;
	String COLUMN_ICON_URL = FriendConstants.ICON_URL;
	String COLUMN_USERNAME = FriendConstants.USERNAME;
	String CREATE_TABLE =
			"CREATE TABLE " + TABLE_NAME
					+ "(" +
					COLUMN_ID + " TEXT PRIMARY KEY," +
					COLUMN_NAME + " TEXT," +
					COLUMN_PASSWORD + " TEXT," +
					COLUMN_EMAIL + " TEXT," +
					COLUMN_USERNAME + " TEXT," +
					COLUMN_COVER_URL + " TEXT," +
					COLUMN_ICON_URL + " TEXT," +
					COLUMN_TOKEN + " TEXT "
					+ ");";
	String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
