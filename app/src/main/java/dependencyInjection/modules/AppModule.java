package dependencyInjection.modules;

import android.app.Application;

import com.facebook.appevents.AppEventsLogger;
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
