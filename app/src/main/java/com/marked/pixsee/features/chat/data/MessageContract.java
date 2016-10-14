package com.marked.pixsee.features.chat.data;

import android.provider.BaseColumns;

import com.marked.pixsee.features.friends.data.FriendContractDB;

/**
 * Created by Tudor on 6/2/2016.
 */
public class MessageContract implements BaseColumns {
	public static final String TABLE_NAME = "messages";
	public static final String COLUMN_ID = MessageConstants.ID;
	public static final String COLUMN_DATA_BODY = MessageConstants.DATA_BODY;
	public static final String COLUMN_TYPE = MessageConstants.MESSAGE_TYPE;
	public static final String COLUMN_DATE = MessageConstants.CREATION_DATE;
	public static final String COLUMN_TO = "_" + MessageConstants.TO;

	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ "("
			+ BaseColumns._ID +
			" INTEGER PRIMARY KEY AUTOINCREMENT," +
			                                          COLUMN_ID + " TEXT UNIQUE,"
			+ COLUMN_DATA_BODY + " TEXT,"
			+ COLUMN_TYPE + " INTEGER,"
			+ COLUMN_DATE + " TEXT,"
			+ COLUMN_TO + " TEXT, FOREIGN KEY( "
			+ COLUMN_TO + ")" + " REFERENCES "
			+ FriendContractDB.TABLE_NAME + "("
			+ FriendContractDB.COLUMN_ID + ")"
			+ ")";


	public static final String DELETE_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

}
