package com.marked.vifo.helper;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

import java.io.File;


public class TheBackupAgent extends BackupAgentHelper {
	// The name of the SharedPreferences file
	static final         String DATABASE_FILE_NAME = "notes.db";
	private static final String DATABASE_FILE_KEY  = "notesdb_key";
    // TODO: 01-Nov-15 create database

	// Allocate a helper and add it to the backup agent
	@Override
	public void onCreate() {
		FileBackupHelper dbs = new FileBackupHelper(this, DATABASE_FILE_NAME);
		addHelper(DATABASE_FILE_KEY, dbs);
	}


	@Override
	public File getFilesDir() {
		File path = getDatabasePath(DATABASE_FILE_NAME);
		return path.getParentFile();
	}
}