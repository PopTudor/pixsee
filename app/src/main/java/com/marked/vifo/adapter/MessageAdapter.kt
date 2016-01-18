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

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
                return TypingHolder(v, context);
            }
            else -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.message_layout_item_right, parent, false);
                return MessageHolder(v, context);
            }
        }
    }

    public override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageHolder ->
                holder.bindMessage(dataSet.get(position));
            is TypingHolder ->
                holder.animate()
        }
    }

    /** If the message has the flag MessageConstants.MessageType.ME then the message will be printed to the right
     * else if the message has the flag MessageConstants.MessageType.YOU then the message will be positioned to the left
     * or if the MessageConstants.MessageType.Typing then the message will be positioned on the left as in You are typing and
     * I wait for you to finish
     * @param position
     * @return
     */
    public override fun getItemViewType(position: Int): Int {
        return dataSet.get(position).messageType;
    }

    public override fun getItemCount(): Int {
        return dataSet.size;
    }


}
