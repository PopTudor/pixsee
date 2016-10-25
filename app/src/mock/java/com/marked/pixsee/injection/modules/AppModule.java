package com.marked.pixsee.injection.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.marked.pixsee.data.database.FakeDatabase;
import com.marked.pixsee.networking.ServerConstants;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tudor on 22-Jul-16.
 */
@Module
public class AppModule {
	Application application;

	public AppModule(Application application) {
		this.application = application;
	}

	@Provides
	@Singleton
	Application providesApplication() {
		return application;
	}

	@Provides
	@Singleton
	OkHttpClient providesHTTPClient() {
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient httpClient = new OkHttpClient.Builder()
				.addInterceptor(loggingInterceptor)
				.build();
		return httpClient;
	}

	@Provides
	@Named(ServerConstants.SERVER)
	@Singleton
	Retrofit providesRetrofit(OkHttpClient client) {
		return new Retrofit.Builder()
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.baseUrl(ServerConstants.SERVER)
				.client(client)
				.build();
	}

	@Provides
	@Singleton
	SharedPreferences provideSharedPreferences() {
		return application.getSharedPreferences("pixsee", Context.MODE_PRIVATE);
	}

	@Provides
	@Singleton
	Gson provideGson() {
		return new Gson();
	}

	@Provides
	@Singleton
	SQLiteOpenHelper provideDatabase() {
		return new FakeDatabase(application, "test", null, 1);
	}
}