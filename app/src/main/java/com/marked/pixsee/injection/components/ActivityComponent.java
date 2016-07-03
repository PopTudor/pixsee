package com.marked.pixsee.injection.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.injection.Local;
import com.marked.pixsee.injection.Remote;
import com.marked.pixsee.injection.Repository;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.injection.scopes.ActivityScope;
import com.marked.pixsee.networking.ServerConstants;

import javax.inject.Named;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by Tudor Pop on 19-Mar-16.
 */
@Component(modules = {ActivityModule.class},dependencies = AppComponent.class)
@ActivityScope
public interface ActivityComponent {
	AppCompatActivity provideAppCompatActivity();

	Context provideContext();

	PixyDatabase provideDatabase();

	@Local
	UserDatasource provideLocalDatasource();

	@Remote
	UserDatasource provideRemoteDatasource();

	@Repository
	UserDatasource provideUserRepository();

	@Named(ServerConstants.SERVER)
	Retrofit retrofit();

	@Named(DatabaseContract.AppsUser.TABLE_NAME)
	User provideUser();

	SharedPreferences provideSharedPreferences();
}
