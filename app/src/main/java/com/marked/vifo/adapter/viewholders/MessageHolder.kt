package com.marked.vifo.adapter.viewholders;

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.library.bubbleview.BubbleTextVew
import com.marked.vifo.R
import com.marked.vifo.extra.MessageConstants
import com.marked.vifo.model.Message

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
public class MessageHolder(itemView: View,context: Context) : RecyclerView.ViewHolder(itemView) {
    private val mMessageTextView = itemView.findViewById(R.id.messageTextView) as BubbleTextVew
    private val mContext = context

	fun bindMessage(message: Message) = mMessageTextView.setText(message.data.get(MessageConstants.DATA_BODY))
}
