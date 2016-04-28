package com.marked.pixsee;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.marked.pixsee.injection.components.AppComponent;
import com.marked.pixsee.injection.components.DaggerAppComponent;
import com.marked.pixsee.injection.modules.AppModule;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public class Pixsee extends Application {
	private AppComponent appComponent;

	@Override
	public void onCreate() {
		super.onCreate();

		appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();

		ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
				.newBuilder(this, new OkHttpClient()).build();
		Fresco.initialize(this, config);
		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);
		//        val fabric = Fabric.Builder(this)
		//				.kits(Crashlytics())
		//				.debuggable(true) // TODO: 13-Dec-15 disable this
		//				.build()
		//		Fabric.with(fabric)
	}

	public AppComponent getAppComponent() {
		return appComponent;
	}
}