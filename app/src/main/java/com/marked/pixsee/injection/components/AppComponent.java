package com.marked.pixsee.injection.components;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.marked.pixsee.injection.modules.AppModule;
import com.marked.pixsee.networking.ServerConstants;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by Tudor Pop on 17-Mar-16.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

	Gson gson();

	SQLiteOpenHelper provideSqLiteOpenHelper();

	SharedPreferences provideSharedPreferences();

	Application application();

	@Named(ServerConstants.SERVER)
	Retrofit retrofit();

	OkHttpClient okhttpclient();
}
