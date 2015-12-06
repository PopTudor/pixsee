package com.marked.vifo.adapter.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.marked.vifo.R;
import com.marked.vifo.extra.MessageConstants;
import com.marked.vifo.model.Message;

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
public class MessageHolder extends RecyclerView.ViewHolder {
	TextView mMessageTextView;
	public MessageHolder(View itemView,final Context context) {
		super(itemView);
		mMessageTextView = (TextView) itemView.findViewById(R.id.messageTextView);


	}
	public void bindContact(Message contact){
		mMessageTextView.setText(contact.getData().get(MessageConstants.MESSAGE_KEY));
	}
}
