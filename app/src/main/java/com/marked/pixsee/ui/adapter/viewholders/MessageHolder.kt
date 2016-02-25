package com.marked.pixsee.ui.adapter.viewholders;

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.marked.pixsee.R
import com.marked.pixsee.extra.MessageConstants
import com.marked.pixsee.model.message.Message

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
class MessageHolder(itemView: View,context: Context) : RecyclerView.ViewHolder(itemView) {
    private val mMessageTextView = itemView.findViewById(R.id.messageTextView) as TextView
    private val mContext = context

	fun bindMessage(message: Message) {
		mMessageTextView.text = message.data.get(MessageConstants.DATA_BODY)
	}
}
