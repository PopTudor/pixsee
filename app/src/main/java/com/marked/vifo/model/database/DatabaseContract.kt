package com.marked.vifo.model.database

import android.provider.BaseColumns
import com.marked.vifo.extra.MessageConstants
import com.marked.vifo.extra.UserConstants

/**
 * Created by Tudor Pop on 26-Jan-16.
 */
class DatabaseContract private constructor() {
	companion object Static {
		const val DATABASE_VERSION = 3
		const val DATABASE_NAME = "pixy.db"

		/* Add to CREATE_TABLE_ARRAY all the other tables that get created and to DELETE_TABLE_ARRAY all tables that get deleted*/
		val CREATE_TABLE_ARRAY = arrayOf(Contact.CREATE_TABLE.trim(), User.CREATE_TABLE.trim(), Message.CREATE_TABLE.trim())
		val DELETE_TABLE_ARRAY = arrayOf(Contact.DELETE_TABLE, User.DELETE_TABLE, Message.DELETE_TABLE)
	}


	/* Contact friends */
	class Contact : BaseColumns {
		companion object Static {
			const val TABLE_NAME = "contact"
			const val COLUMN_ID = UserConstants.ID /* id from online stored database */
			const val COLUMN_NAME = UserConstants.NAME
			const val COLUMN_TOKEN = UserConstants.TOKEN

			const val CREATE_TABLE = """
			CREATE TABLE ${TABLE_NAME}(
			${COLUMN_ID} TEXT PRIMARY KEY,
			${COLUMN_NAME} TEXT,
			${COLUMN_TOKEN} TEXT
			);"""

			const val DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
		}
	}

	/* App's user*/
	class User : BaseColumns {
		companion object Static {
			const val TABLE_NAME = "user"
			const val COLUMN_ID = UserConstants.ID
			const val COLUMN_NAME = UserConstants.NAME
			const val COLUMN_TOKEN = UserConstants.TOKEN

			const val CREATE_TABLE = """
			CREATE TABLE ${TABLE_NAME}(
			${COLUMN_ID} TEXT PRIMARY KEY,
			${COLUMN_NAME} TEXT,
			${COLUMN_TOKEN} TEXT
			);"""
			const val DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
		}
	}

	/* Message */
	class Message : BaseColumns {
		companion object Static {
			const val TABLE_NAME = "message"
			const val COLUMN_DATA_BODY = MessageConstants.DATA_BODY
			const val COLUMN_TYPE = MessageConstants.MESSAGE_TYPE
			const val COLUMN_DATE = MessageConstants.CREATION_DATE
			const val COLUMN_TO = "_" + MessageConstants.TO

			const val CREATE_TABLE = """
			CREATE TABLE $TABLE_NAME(
			${BaseColumns._ID} INTEGER PRIMARY KEY,
			$COLUMN_DATA_BODY TEXT,
			$COLUMN_TYPE INTEGER,
			$COLUMN_DATE INTEGER,
			$COLUMN_TO TEXT,
			FOREIGN KEY($COLUMN_TO) REFERENCES ${Contact.TABLE_NAME}(${Contact.COLUMN_ID})
			);"""

			const val DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
		}
	}


}