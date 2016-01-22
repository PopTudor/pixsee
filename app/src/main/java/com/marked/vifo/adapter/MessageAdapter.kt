package com.marked.vifo.adapter;

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marked.vifo.R
import com.marked.vifo.adapter.viewholders.MessageHolder
import com.marked.vifo.adapter.viewholders.TypingHolder
import com.marked.vifo.extra.MessageConstants
import com.marked.vifo.model.Message


/**
 * Created by Tudor Pop on 04-Dec-15.
 */
public class MessageAdapter(context: Context, dataSet: kotlin.List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	val dataSet = dataSet
	val context = context

	override
	fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		// Create a new view.
		var v: View;
		when (viewType) {
			MessageConstants.MessageType.ME -> {
				v = LayoutInflater.from(parent.context).inflate(R.layout.message_layout_item_right, parent, false);
				return MessageHolder(v, context);
			}
			MessageConstants.MessageType.YOU -> {
				v = LayoutInflater.from(parent.context).inflate(R.layout.message_layout_item_left, parent, false);
				return MessageHolder(v, context);
			}
			MessageConstants.MessageType.TYPING -> {
				v = LayoutInflater.from(parent.context).inflate(R.layout.message_layout_typing, parent, false);
				return TypingHolder(v);
			}
			else -> {
				v = LayoutInflater.from(parent.context).inflate(com.marked.vifo.R.layout.message_layout_item_right, parent, false);
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
			is TypingHolder ->
				holder.start()
		}
	}

	/** If the message has the flag MessageConstants.MessageType.ME then the message will be printed to the right
	 * else if the message has the flag MessageConstants.MessageType.YOU then the message will be positioned to the left
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
