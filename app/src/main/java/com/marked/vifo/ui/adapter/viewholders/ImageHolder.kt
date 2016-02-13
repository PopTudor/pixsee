package com.marked.vifo.ui.adapter.viewholders;

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.marked.vifo.R
import com.marked.vifo.extra.MessageConstants
import com.marked.vifo.model.Message
import com.marked.vifo.model.requestQueue

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
class ImageHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
	private val mImage = itemView.findViewById(R.id.imageNetwork) as NetworkImageView
	private val mContext = context

	fun bindMessage(message: Message) {
		val url = message.data.get(MessageConstants.DATA_BODY)
		mImage.setImageUrl(url, ImageLoader(mContext.requestQueue.queue, LruBitmapCache(LruBitmapCache.getCacheSize(mContext))))
	}

}
