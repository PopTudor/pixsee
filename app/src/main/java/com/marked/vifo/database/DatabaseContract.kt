package com.marked.vifo.database

import android.provider.BaseColumns

/**
 * Created by Tudor Pop on 26-Jan-16.
 */
class DatabaseContract private constructor() {

	class Message : BaseColumns {

		companion object : BaseColumns {

			val TABLE_NAME = "message"
			val COLUMN_ID = "messageID"
			val COLUMN_DATA_BODY = "body"
			val COLUMN_TYPE = "type"
			val COLUMN_DATE = "date"
			val COLUMN_TO = "to"

			val CREATE_MESSAGE = """CREATE TABLE $TABLE_NAME
			(${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_ID

			"""

		}
	}

}