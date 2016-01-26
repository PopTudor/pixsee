package com.marked.vifo.database

import android.provider.BaseColumns

/**
 * Created by Tudor Pop on 26-Jan-16.
 */
class DatabaseContract private constructor() {

	abstract class Message : BaseColumns {
		companion object {
			val TABLE_NAME = "message"
			val COLUMN_ID = "id"
		}
	}

}