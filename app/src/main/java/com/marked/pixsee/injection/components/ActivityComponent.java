package com.marked.pixsee.injection.components;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.injection.scopes.ActivityScope;
import com.marked.pixsee.injection.scopes.Local;
import com.marked.pixsee.injection.scopes.Remote;
import com.marked.pixsee.injection.scopes.Repository;

import javax.inject.Named;

import dagger.Component;

/**
 * Created by Tudor Pop on 19-Mar-16.
 */
@Component(modules = {ActivityModule.class},dependencies = AppComponent.class)
@ActivityScope
public interface ActivityComponent extends AppComponent {
	AppCompatActivity provideAppCompatActivity();

	Context provideContext();

	@Local
	UserDatasource provideLocalDatasource();

	@Remote
	UserDatasource provideRemoteDatasource();

	@Repository
	UserDatasource provideUserRepository();

	@Named(DatabaseContract.AppsUser.TABLE_NAME)
	User provideUser();

}
