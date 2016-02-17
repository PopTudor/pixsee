package com.marked.vifo.ui.adapter.viewholders;

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import com.facebook.drawee.view.SimpleDraweeView
import com.marked.vifo.R
import com.marked.vifo.extra.MessageConstants
import com.marked.vifo.model.message.Message

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
class ImageHolder(itemView: View,val context: Context) : RecyclerView.ViewHolder(itemView) {
	private val mImage = itemView.findViewById(R.id.imageNetwork) as SimpleDraweeView

	fun bindMessage(message: Message) {
		val url = Uri.parse(message.data.get(MessageConstants.DATA_BODY));
		mImage.setImageURI(url,context);
	}

}
