package com.marked.vifo.ui.adapter;

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marked.vifo.R
import com.marked.vifo.extra.MessageConstants
import com.marked.vifo.model.message.Message
import com.marked.vifo.ui.adapter.viewholders.ImageHolder
import com.marked.vifo.ui.adapter.viewholders.MessageHolder
import com.marked.vifo.ui.adapter.viewholders.TypingHolder


/**
 * Created by Tudor Pop on 04-Dec-15.
 */
class MessageAdapter(private val context: Context, private val dataSet: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	override
	fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		// Create a new view.
		var v: View
		when (viewType) {
			MessageConstants.MessageType.ME_MESSAGE -> {
				v = LayoutInflater.from(parent.context).inflate(R.layout.message_mine_text, parent, false);
				return MessageHolder(v, context);
			}
			MessageConstants.MessageType.YOU_MESSAGE -> {
				v = LayoutInflater.from(parent.context).inflate(R.layout.message_other_text, parent, false);
				return MessageHolder(v, context);
			}
			MessageConstants.MessageType.ME_IMAGE -> {
				v = LayoutInflater.from(parent.context).inflate(R.layout.message_mine_image, parent, false)
				return ImageHolder(v, context)
			}
			MessageConstants.MessageType.YOU_IMAGE -> {
				v = LayoutInflater.from(parent.context).inflate(R.layout.message_other_image, parent, false)
				return ImageHolder(v, context)
			}
			MessageConstants.MessageType.TYPING -> {
				v = LayoutInflater.from(parent.context).inflate(R.layout.message_typing, parent, false);
				return TypingHolder(v);
			}
			else -> {
				v = LayoutInflater.from(parent.context).inflate(com.marked.vifo.R.layout.message_other_text, parent, false);
				return MessageHolder(v, context);
			}
		}
	}

	override
	fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder?) {
		super.onViewDetachedFromWindow(holder)
		if (holder is TypingHolder) {
			/* must call stop and set recyclable to false because else will cache the 'typing animation' and it will not stop or it will block*/
			holder.stop()
			holder.setIsRecyclable(false)
		} else
			holder?.setIsRecyclable(true)
	}

	override
	fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when (holder) {
			is MessageHolder ->
				holder.bindMessage(dataSet.get(position));
			is ImageHolder ->
				holder.bindMessage(dataSet.get(position))
			is TypingHolder ->
				holder.start()
		}
	}

	/** If the message has the flag MessageConstants.MessageType.ME_MESSAGE then the message will be printed to the right
	 * else if the message has the flag MessageConstants.MessageType.YOU_MESSAGE then the message will be positioned to the left
	 * or if the MessageConstants.MessageType.Typing then the message will be positioned on the left as in You are typing and
	 * I wait for you to finish
	 * @param position
	 * @return
	 */
	override
	fun getItemViewType(position: Int): Int {
		return dataSet.get(position).messageType;
	}

	override
	fun getItemCount(): Int {
		return dataSet.size;
	}
}
