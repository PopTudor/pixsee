package com.marked.pixsee.utility;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

import java.io.File;


class TheBackupAgent extends BackupAgentHelper {
	// TODO: 01-Nov-15 create database

	// Allocate a helper and add it to the backup agent

    @Override
    public void onCreate() {
        super.onCreate();
        FileBackupHelper dbs = new FileBackupHelper(this, DATABASE_FILE_NAME);
        addHelper(DATABASE_FILE_KEY, dbs);
    }

	public File getFilesDir() {
        File path = getDatabasePath(DATABASE_FILE_NAME);
        return path.getParentFile();
    }

    // The name of the SharedPreferences file
    static String DATABASE_FILE_NAME = "pixsee.db";
    private String DATABASE_FILE_KEY = "pixseedb_key";
}