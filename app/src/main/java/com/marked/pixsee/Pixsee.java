package com.marked.pixsee;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.marked.pixsee.di.components.AppComponent;
import com.marked.pixsee.di.components.DaggerAppComponent;
import com.marked.pixsee.di.modules.AppModule;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public class Pixsee extends Application {
	private AppComponent appComponent;

	@Override
	public void onCreate() {
		super.onCreate();

		appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
		OkHttpClient client = new OkHttpClient();
		client.interceptors().add(new com.squareup.okhttp.Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Request request = chain.request();

				long t1 = System.nanoTime();
				Log.i("OKHttp",String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

				Response response = chain.proceed(request);

				long t2 = System.nanoTime();
				Log.i("OKHttp",String.format("Received response for %s in %.1fms%n%s",response.request().url(), (t2 - t1) / 1e6d, response.headers()));

				return response;
			}
		});
		ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
				.newBuilder(this, client)
				.setDownsampleEnabled(true)
				.build();
		Fresco.initialize(this, config);
		FacebookSdk.sdkInitialize(getApplicationContext());
		Fabric.with(this, new Crashlytics());
	}

	public AppComponent getAppComponent() {
		return appComponent;
	}
}