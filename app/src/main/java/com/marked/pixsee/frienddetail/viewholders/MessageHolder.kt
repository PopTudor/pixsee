package com.marked.pixsee.frienddetail.viewholders;

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.marked.pixsee.R
import com.marked.pixsee.data.message.Message
import com.marked.pixsee.data.message.MessageConstants

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
class MessageHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    private val mMessageTextView = itemView.findViewById(R.id.messageTextView) as TextView
    private val mContext = context

	fun bindMessage(message: Message) {
		mMessageTextView.text = message.data.get(MessageConstants.DATA_BODY)
	}
}
