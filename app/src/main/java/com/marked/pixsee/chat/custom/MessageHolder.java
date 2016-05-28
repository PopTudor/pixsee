package com.marked.pixsee.chat.custom;

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
	private TextView mMessageTextView ;

	public MessageHolder(View itemView) {
		super(itemView);
		mMessageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
	}

	public void bindMessage(final Message message, final ChatAdapter.ChatInteraction chatInteraction) {
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chatInteraction.chatClicked(message);
			}
		});
		mMessageTextView.setText(message.getData().get(MessageConstants.DATA_BODY));
	}
}
