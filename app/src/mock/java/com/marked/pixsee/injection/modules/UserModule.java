package com.marked.pixsee.injection.modules;

import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.marked.pixsee.data.chat.FakeChatDiskDatasource;
import com.marked.pixsee.data.user.FakeUserDatasource;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.injection.scopes.Local;
import com.marked.pixsee.injection.scopes.Remote;
import com.marked.pixsee.injection.scopes.Repository;
import com.marked.pixsee.injection.scopes.Session;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.ui.chat.data.ChatDiskDatasource;

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
		return new FakeUserDatasource();
	}

	@Provides
	@Session
	@Remote
	UserDatasource provideUserRepositoryRemote(UserManager userManager, @Named(ServerConstants.SERVER) Retrofit retrofit, Gson gson) {
		return new FakeUserDatasource();
	}

	@Provides
	@Session
	@Repository
	UserDatasource provideUserRepository(@Local UserDatasource local, @Remote UserDatasource remote) {
		return new FakeUserDatasource();
	}

	@Provides
	@Session
	ChatDiskDatasource mChatDiskDatasource(SQLiteOpenHelper sqLiteOpenHelper) {
		return new FakeChatDiskDatasource(sqLiteOpenHelper);
	}
}
