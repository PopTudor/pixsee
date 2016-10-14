package com.marked.pixsee.features.chat.custom;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.marked.pixsee.R;
import com.marked.pixsee.features.chat.data.Message;
import com.marked.pixsee.features.chat.data.MessageConstants;


/**
 * Created by Tudor Pop on 04-Dec-15.
 */
public class MessageHolder extends RecyclerView.ViewHolder implements Bindable{
	private TextView mMessageTextView;

	public MessageHolder(View itemView) {
		super(itemView);
		mMessageTextView = (TextView) itemView.findViewById(R.id.messageTextView);

	}

	@Override
	public void bindMessage(final Message message, final ChatAdapter.ChatInteraction chatInteraction) {
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chatInteraction.chatClicked(v, message, getAdapterPosition());
			}
		});
		mMessageTextView.setText(message.getData().get(MessageConstants.DATA_BODY));
	}

}
