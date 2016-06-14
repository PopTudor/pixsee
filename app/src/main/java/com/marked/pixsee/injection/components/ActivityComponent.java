package com.marked.pixsee.injection.components;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.data.repository.user.UserRepository;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.injection.scopes.PerActivity;

import dagger.Component;

/**
 * Created by Tudor Pop on 19-Mar-16.
 */
@Component(modules = {ActivityModule.class})
@PerActivity
public interface ActivityComponent {
	AppCompatActivity provideAppCompatActivity();

	Context provideContext();

	PixyDatabase provideDatabase();

	UserRepository provideUserRepository();

	User provideUser();
}
