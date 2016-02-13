package com.marked.vifo.ui.adapter.viewholders

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.util.LruCache
import android.util.DisplayMetrics
import com.android.volley.toolbox.ImageLoader

class LruBitmapCache(size: Int) : LruCache<String, Bitmap>(size), ImageLoader.ImageCache {
	constructor(context: Context) : this(getCacheSize(context))

	override fun sizeOf(key: String, value: Bitmap): Int = value.rowBytes * value.height

	override fun getBitmap(url: String?): Bitmap? = get(url)

	override fun putBitmap(url: String?, bitmap: Bitmap?) {
		put(url, bitmap)
	}

	companion object {
		// Returns a cache size equal to approximately three screens worth of images.
		fun getCacheSize(context: Context): Int {
			val displayMetrics: DisplayMetrics = context.resources.displayMetrics;
			val screenWidth = displayMetrics.widthPixels;
			val screenHeight = displayMetrics.heightPixels;
			// 4 bytes per pixel
			val screenBytes = screenWidth * screenHeight * 4;

			return screenBytes * 3;
		}
	}
}