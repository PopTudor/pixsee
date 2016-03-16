package com.marked.pixsee;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public class Pixsee extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory.newBuilder(this, new OkHttpClient()).build();
        Fresco.initialize(this, config);
//        val fabric = Fabric.Builder(this)
        //				.kits(Crashlytics())
        //				.debuggable(true) // TODO: 13-Dec-15 disable this
        //				.build()
        //		Fabric.with(fabric)
    }
}