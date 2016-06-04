package com.marked.pixsee.injection.components;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.injection.scopes.PerActivity;
import com.marked.pixsee.login.LogInActivity;

import dagger.Component;

/**
 * Created by Tudor Pop on 19-Mar-16.
 */
@Component(modules = {ActivityModule.class})
@PerActivity
public interface ActivityComponent {
	void inject(LogInActivity activity);

	AppCompatActivity provideAppCompatActivity();

	Context provideContext();

	PixyDatabase provideDatabase();
}
