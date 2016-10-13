package com.marked.pixsee.di.modules;

import android.app.Application;

import com.marked.pixsee.PixseeTest;
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
public class FakeAppModule {
	Application mApplication;

	public FakeAppModule(PixseeTest application) {
		mApplication = application;
	}

	@Provides
	@Singleton
	Application providesApplication() {
		return mApplication;
	}

	@Provides
	@Singleton
	okhttp3.OkHttpClient providesHTTPClient() {
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient.Builder()
				.addInterceptor(loggingInterceptor)
				.build();
		return httpClient;
	}

	@Provides
	@Named(ServerConstants.SERVER)
	Retrofit providesRetrofit(OkHttpClient client) {
		return new Retrofit.Builder()
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.baseUrl(ServerConstants.SERVER)
				.client(client)
				.build();
	}
}