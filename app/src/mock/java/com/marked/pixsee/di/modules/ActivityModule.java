package com.marked.pixsee.injection.modules;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.FakeUser;
import com.marked.pixsee.data.user.FakeUserDatasource;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.injection.scopes.ActivityScope;
import com.marked.pixsee.injection.scopes.Local;
import com.marked.pixsee.injection.scopes.Remote;
import com.marked.pixsee.injection.scopes.Repository;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;


/**
 * Created by Tudor on 24-Jul-16.
 */
@Module
@ActivityScope
public class ActivityModule {
	private AppCompatActivity activity;

	public ActivityModule(AppCompatActivity activity) {
		this.activity = activity;
	}

	@Provides
	@ActivityScope
	ProgressDialog provideProgressDialog() {
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setTitle("Login");
		dialog.setMessage("Please wait...");
		dialog.setIndeterminate(true);
		return dialog;
	}

	@Provides
	@ActivityScope
	LocalBroadcastManager provideLocalBroadcastManager() {
		return LocalBroadcastManager.getInstance(activity);
	}

	@Provides
	@ActivityScope
	SharedPreferences provideSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(activity);
	}

	@Provides
	@ActivityScope
	AppCompatActivity provideActivity() {
		return activity;
	}

	@Provides
	@ActivityScope
	Context provideContext() {
		return activity;
	}

	@Provides
	@ActivityScope
	SQLiteOpenHelper provideDatabase() {
		return new FakeDatabase(activity,"test",null,1);
	}

	@Provides
	@ActivityScope
	@Local
	UserDatasource provideUserRepositoryLocal(SQLiteOpenHelper database) {
		return new FakeUserDatasource();
	}
	@Provides
	@ActivityScope
	@Remote
	UserDatasource provideUserRepositoryRemote(SharedPreferences preferences) {
		return new FakeUserDatasource();
	}

	@Provides
	@ActivityScope
	@Repository
	UserDatasource provideUserRepository(@Local UserDatasource local, @Remote UserDatasource remote) {
		return new FakeUserDatasource();
	}

	@Provides
	@ActivityScope
	User provideAppsUser(@Repository UserDatasource repository){
		return new FakeUser();
	}

	private class FakeDatabase extends SQLiteOpenHelper{
		public FakeDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		public FakeDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
			super(context, name, factory, version, errorHandler);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}
}
