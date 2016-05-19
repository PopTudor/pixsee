package com.marked.pixsee.friends.cards;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.marked.pixsee.R;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.data.message.MessageConstants;

/**
 * Created by Tudor on 2016-05-19.
 */
public class CardVH extends RecyclerView.ViewHolder {
	private CardAdapter.MessageInteraction messageInteraction;
	private TextView titleTextView;
	private TextView bodyTextView;
	private ImageButton moreImageButton;
	private ImageButton favoriteImageButton;
	private ImageButton replyImageButton;

	public CardVH(View itemView, CardAdapter.MessageInteraction messageInteraction) {
		super(itemView);
		this.messageInteraction = messageInteraction;
		titleTextView = (TextView) itemView.findViewById(R.id.titleTextVIew);
		bodyTextView = (TextView) itemView.findViewById(R.id.bodyTextView);
		moreImageButton = (ImageButton) itemView.findViewById(R.id.moreImageButton);
		favoriteImageButton = (ImageButton) itemView.findViewById(R.id.favoriteImageButton);
		replyImageButton = (ImageButton) itemView.findViewById(R.id.quickReplyImageButton);
	}

	public void bind(final Message message) {
		titleTextView.setText(DateUtils.formatElapsedTime(message.getDate()));
		bodyTextView.setText(message.getData().get(MessageConstants.DATA_BODY));
		moreImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				messageInteraction.moreClicked(message);
			}
		});
		favoriteImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				messageInteraction.moreClicked(message);
			}
		});
		replyImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				messageInteraction.moreClicked(message);
			}
		});
	}

}
