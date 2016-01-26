package com.marked.vifo.database

import android.provider.BaseColumns

/**
 * Created by Tudor Pop on 26-Jan-16.
 */
class DatabaseContract private constructor() {
	companion object Static {
		const val DATABASE_VERSION = 1
		const val DATABASE_NAME = "pixy.db"

		val CREATE_TABLE_ARRAY = arrayOf(Contact.CREATE_TABLE, User.CREATE_TABLE, Message.CREATE_TABLE)
		val DELETE_TABLE_ARRAY = arrayOf(Contact.DELETE_TABLE, User.DELETE_TABLE, Message.DELETE_TABLE)
	}


	/* Contact friends */
	class Contact : BaseColumns {
		companion object Static {
			const val TABLE_NAME = "contact"
			const val COLUMN_ID = "objID" /* id from online stored database */
			const val COLUMN_FIRST_NAME = "firstName"
			const val COLUMN_LAST_NAME = "lastName"
			const val COLUMN_TOKEN = "token"

			const val CREATE_TABLE = """
			CREATE TABLE ${TABLE_NAME}(
			${BaseColumns._ID} TEXT PRIMARY KEY,
			${COLUMN_ID} TEXT UNIQUE,
			${COLUMN_FIRST_NAME} TEXT,
			${COLUMN_LAST_NAME} TEXT,
			${COLUMN_TOKEN} TEXT
			);"""

			const val DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
		}
	}

	/* App's user*/
	class User : BaseColumns {
		companion object Static {
			const val TABLE_NAME = "user"
			const val COLUMN_FIRST_NAME = "firstName"
			const val COLUMN_LAST_NAME = "lastName"
			const val COLUMN_TOKEN = "token"
			const val COLUMN_ID = "objID"

			const val CREATE_TABLE = """
			CREATE TABLE ${TABLE_NAME}(
			${BaseColumns._ID} INTEGER PRIMARY KEY,
			${COLUMN_ID} TEXT,
			${COLUMN_FIRST_NAME} TEXT,
			${COLUMN_LAST_NAME} TEXT,
			${COLUMN_TOKEN} TEXT
			);"""
			const val DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
		}
	}

	/* Message */
	class Message : BaseColumns {
		companion object Static {
			const val TABLE_NAME = "message"
			const val COLUMN_DATA_BODY = "body"
			const val COLUMN_TYPE = "type"
			const val COLUMN_DATE = "creationDate"
			const val COLUMN_TO = "destination"

			const val CREATE_TABLE = """
			CREATE TABLE $TABLE_NAME(
			${BaseColumns._ID} INTEGER PRIMARY KEY,
			$COLUMN_DATA_BODY TEXT,
			$COLUMN_TYPE INTEGER,
			$COLUMN_DATE DATE,
			$COLUMN_TO TEXT UNIQUE,
			FOREIGN KEY($COLUMN_TO) REFERENCES ${Contact.TABLE_NAME}(${Contact.COLUMN_ID})
			);"""

			const val DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
		}
	}


}