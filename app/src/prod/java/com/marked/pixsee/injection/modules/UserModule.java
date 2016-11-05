package com.marked.pixsee.injection.modules;

import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.marked.pixsee.data.friends.FriendsAPI;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.data.user.UserDiskDatasource;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.data.user.UserNetworkDatasource;
import com.marked.pixsee.data.user.UserRepository;
import com.marked.pixsee.injection.scopes.Local;
import com.marked.pixsee.injection.scopes.Remote;
import com.marked.pixsee.injection.scopes.Repository;
import com.marked.pixsee.injection.scopes.Session;
import com.marked.pixsee.networking.ServerConstants;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

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
	UserDatasource provideUserRepositoryRemote(UserManager userManager, @Named(ServerConstants.SERVER) Retrofit retrofit, Gson gson) {
		return new UserNetworkDatasource(userManager, retrofit.create(FriendsAPI.class), gson);
	}

	@Provides
	@Session
	@Repository
	UserDatasource provideUserRepository(@Local UserDatasource local, @Remote UserDatasource remote) {
		return new UserRepository(local, remote);
	}
}
