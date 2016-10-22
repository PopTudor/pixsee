package com.marked.pixsee.injection.modules;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.data.user.UserDiskDatasource;
import com.marked.pixsee.data.user.UserNetworkDatasource;
import com.marked.pixsee.data.user.UserRepository;
import com.marked.pixsee.injection.scopes.Local;
import com.marked.pixsee.injection.scopes.Remote;
import com.marked.pixsee.injection.scopes.Repository;
import com.marked.pixsee.injection.scopes.Session;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 21-Oct-16.
 */
@Module
@Session
public class UserModule {

	@Provides
	@Session
	@Local
	UserDatasource provideUserRepositoryLocal(SQLiteOpenHelper database) {
		return new UserDiskDatasource(database);
	}

	@Provides
	@Session
	@Remote
	UserDatasource provideUserRepositoryRemote(SharedPreferences preferences) {
		return new UserNetworkDatasource(preferences);
	}

	@Provides
	@Session
	@Repository
	UserDatasource provideUserRepository(@Local UserDatasource local, @Remote UserDatasource remote) {
		return new UserRepository(local, remote);
	}

	@Provides
	@Session
	User provideAppsUser(@Repository UserDatasource repository) {
		return repository.getUser(DatabaseContract.AppsUser.TABLE_NAME);
	}
}
