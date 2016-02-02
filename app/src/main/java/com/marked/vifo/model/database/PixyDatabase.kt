package com.marked.vifo.model.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper

/**
 * Created by Tudor Pop on 26-Jan-16.
 */
class PixyDatabase private constructor(context: Context) : ManagedSQLiteOpenHelper(context, DatabaseContract.DATABASE_NAME, version = DatabaseContract.DATABASE_VERSION) {
	companion object {
		private var instance: PixyDatabase? = null

		@Synchronized
		fun getInstance(context: Context): PixyDatabase {
			if (instance == null)
				instance = PixyDatabase(context.applicationContext)
			return instance!!
		}
	}

	override fun onCreate(db: SQLiteDatabase) {
		// Here you create tables (more info about that is below)
		for (it in DatabaseContract.CREATE_TABLE_ARRAY)
			db.execSQL(it)
	}

	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		// Here you can upgrade tables, as usual
		for (it in DatabaseContract.DELETE_TABLE_ARRAY)
			db.execSQL(it);
		onCreate(db);
	}

}

val Context.database: PixyDatabase
	get() = PixyDatabase.getInstance(applicationContext)
