package com.marked.pixsee.data.database;

import com.marked.pixsee.ui.chat.data.MessageContract;
import com.marked.pixsee.ui.friends.data.FriendContractDB;

/**
 * Created by Tudor Pop on 26-Jan-16.
 */
public class DatabaseContract {

	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "pixy.db";
	/* Add to CREATE_TABLE_ARRAY all the other tables that get created and to DELETE_TABLE_ARRAY all tables that get deleted*/
	public static final String[] CREATE_TABLE_ARRAY = {FriendContractDB.CREATE_TABLE.trim(), MessageContract.CREATE_TABLE.trim()};
	public static final String[] DELETE_TABLE_ARRAY = {FriendContractDB.DELETE_TABLE, MessageContract.DELETE_TABLE};

	private DatabaseContract() {
	}
}