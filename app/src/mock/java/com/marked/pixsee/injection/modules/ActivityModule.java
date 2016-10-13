package com.marked.pixsee.di.modules;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.model.database.DatabaseContract;
import com.marked.pixsee.model.database.PixyDatabase;
import com.marked.pixsee.model.user.User;
import com.marked.pixsee.model.user.UserDatasource;
import com.marked.pixsee.model.user.UserDatasourceFake;
import com.marked.pixsee.model.user.UserDiskDatasource;
import com.marked.pixsee.model.user.UserRepository;
import com.marked.pixsee.di.injection.Local;
import com.marked.pixsee.di.injection.Remote;
import com.marked.pixsee.di.injection.Repository;
import com.marked.pixsee.di.scopes.ActivityScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor Pop on 19-Mar-16.
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
		return PixyDatabase.getInstance(activity);
	}

	@Provides
	@ActivityScope
	@Local
	UserDatasource provideUserRepositoryLocal(SQLiteOpenHelper database) {
		return new UserDiskDatasource(database);
	}
	@Provides
	@ActivityScope
	@Remote
	UserDatasource provideUserRepositoryRemote(SharedPreferences preferences) {
		return new UserDatasourceFake();
	}

	@Provides
	@ActivityScope
	@Repository
	UserDatasource provideUserRepository(@Local UserDatasource local, @Remote UserDatasource remote) {
		return new UserRepository(local,remote);
	}
	@Provides
	@ActivityScope
	@Named(DatabaseContract.AppsUser.TABLE_NAME)
	User provideAppsUser(@Repository UserDatasource repository){
		return UserUtilTest.getUserTest();
	}


	public static class UserUtilTest {
		public static final String USER_ID = "user_id_123";
		public static final String USER_NAME = "user_name";
		public static final String USER_USERNAME = "user_username";
		public static final String USER_EMAIL = "user_email";
		public static final String USER_COVERURL = "user_coverurl";
		public static final String USER_ICONURL = "user_iconurl";
		public static final String USER_TOKEN = "user_token";
		public static final String USER_PASSWORD = "user_password";

		public static User getUserTest() {
			return new User(USER_ID, USER_NAME, USER_EMAIL, USER_TOKEN, USER_PASSWORD, USER_COVERURL, USER_ICONURL, USER_USERNAME);
		}
	}
}
