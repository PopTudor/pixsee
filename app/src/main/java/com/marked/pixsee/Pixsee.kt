package com.marked.pixsee

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory
import com.squareup.okhttp.OkHttpClient

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
class Pixsee : Application() {

	override fun onCreate() {
		super.onCreate()
		var config = OkHttpImagePipelineConfigFactory.newBuilder(this, OkHttpClient()).build()
		Fresco.initialize(this,config)
        //		val fabric = Fabric.Builder(this)
        //				.kits(Crashlytics())
        //				.debuggable(true) // TODO: 13-Dec-15 disable this
        //				.build()
        //		Fabric.with(fabric)
	}
}