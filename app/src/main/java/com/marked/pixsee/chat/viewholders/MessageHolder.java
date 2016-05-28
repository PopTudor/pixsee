package com.marked.pixsee.chat.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.marked.pixsee.R;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.data.MessageConstants;


/**
 * Created by Tudor Pop on 04-Dec-15.
 */
public class MessageHolder extends RecyclerView.ViewHolder {
	private Context context;
	private TextView mMessageTextView ;
	public MessageHolder(View itemView, Context context) {
		super(itemView);
		this.context = context;
		mMessageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
	}

	public void bindMessage(Message message) {
		mMessageTextView.setText(message.getData().get(MessageConstants.DATA_BODY));
	}
}
