package com.marked.vifo

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
class Pixsee : Application() {
	override fun onCreate() {
		super.onCreate()
		Fresco.initialize(this)
	}
}