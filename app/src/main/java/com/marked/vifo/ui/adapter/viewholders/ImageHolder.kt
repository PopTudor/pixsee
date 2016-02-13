package com.marked.vifo.ui.adapter.viewholders;

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.util.LruCache
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.View
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.ImageLoader.ImageCache
import com.android.volley.toolbox.NetworkImageView
import com.marked.vifo.R
import com.marked.vifo.extra.MessageConstants
import com.marked.vifo.model.Message
import com.marked.vifo.model.RequestQueueAccess

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
class ImageHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
	private val mImage = itemView.findViewById(R.id.imageNetwork) as NetworkImageView
	private val mContext = context

	fun bindMessage(message: Message) {
		val url = message.data.get(MessageConstants.DATA_BODY)


		mImage.setImageUrl(url, ImageLoader(RequestQueueAccess.getInstance(mContext).queue,LruBitmapCache(LruBitmapCache.getCacheSize(mContext))))
	}

	class LruBitmapCache(size: Int) : LruCache<String, Bitmap>(size), ImageCache {
		constructor(context: Context) : this(getCacheSize(context))


		override fun sizeOf(key: String, value: Bitmap): Int {
			return value.rowBytes * value.height;
		}

		override fun getBitmap(url: String): Bitmap = get(url)

		override fun putBitmap(url: String, bitmap: Bitmap) {
			put(url, bitmap);
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
}
