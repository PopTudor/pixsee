package com.marked.pixsee.injection.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;

import com.facebook.appevents.AppEventsLogger;
import com.google.gson.Gson;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.networking.ServerConstants;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tudor Pop on 16-Mar-16.
 */
@Module
public class AppModule {
	private Application application;

	public AppModule(Application application) {
		this.application = application;
	}

	@Provides
	@Singleton
	Application providesApplication() {
		AppEventsLogger.activateApp(application);
		return application;
	}

	@Provides
	@Singleton
	OkHttpClient providesHTTPClient() {
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		return new OkHttpClient.Builder()
				       .authenticator(new BasicAuthenticator())
				       .addInterceptor(new BasicInterceptor())
				       .addInterceptor(loggingInterceptor)
				       .build();
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

	@Provides
	@Singleton
	SharedPreferences provideSharedPreferences() {
		return application.getSharedPreferences("pixsee", Context.MODE_PRIVATE);
	}

	@Provides
	@Singleton
	SQLiteOpenHelper provideDatabase() {
		return PixyDatabase.getInstance(application);
	}

	@Provides
	@Singleton
	Gson provideGson() {
		return new Gson();
	}

	private class BasicAuthenticator implements Authenticator {
		@Override
		public Request authenticate(Route route, Response response) throws IOException {
//			response.close();
			return response.request()
					       .newBuilder()
					       .header("Authorization", Credentials.basic("USER", "parola"))
					       .build();
		}
	}

	private class BasicInterceptor implements Interceptor {
		@Override
		public Response intercept(Chain chain) throws IOException {
			Request request = chain.request().newBuilder()
					                  .header("Authorization", Credentials.basic("USER", "parola"))
					                  .build();
			return chain.proceed(request);
		}
	}
}
