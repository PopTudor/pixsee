package com.marked.pixsee.utility

import android.app.backup.BackupAgentHelper
import android.app.backup.FileBackupHelper

import java.io.File


class TheBackupAgent : BackupAgentHelper() {
	// TODO: 01-Nov-15 create database

	// Allocate a helper and add it to the backup agent
	override fun onCreate() {
		val dbs = FileBackupHelper(this, DATABASE_FILE_NAME)
		addHelper(DATABASE_FILE_KEY, dbs)
	}


	override fun getFilesDir(): File {
		val path = getDatabasePath(DATABASE_FILE_NAME)
		return path.parentFile
	}

	companion object {
		// The name of the SharedPreferences file
		internal val DATABASE_FILE_NAME = "pixsee.db"
		private val DATABASE_FILE_KEY = "pixseedb_key"
	}
}