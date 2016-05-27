package com.marked.pixsee.frienddetail.cards;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.marked.pixsee.R;
import com.marked.pixsee.frienddetail.data.Message;
import com.marked.pixsee.frienddetail.data.MessageConstants;

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
	private Context context;

	public CardVH(View itemView, CardAdapter.MessageInteraction messageInteraction) {
		super(itemView);
		context = itemView.getContext();
		this.messageInteraction = messageInteraction;
		titleTextView = (TextView) itemView.findViewById(R.id.titleTextVIew);
		bodyTextView = (TextView) itemView.findViewById(R.id.friendBodyTextView);
		moreImageButton = (ImageButton) itemView.findViewById(R.id.moreImageButton);
		favoriteImageButton = (ImageButton) itemView.findViewById(R.id.favoriteImageButton);
		replyImageButton = (ImageButton) itemView.findViewById(R.id.quickReplyImageButton);
	}

	public void bind(final Message message) {
		titleTextView.setText(DateUtils.formatDateTime(context,Long.parseLong(message.getDate()),DateUtils.FORMAT_SHOW_DATE|
				                                                                                         DateUtils.FORMAT_24HOUR|
				                                                                                         DateUtils.FORMAT_SHOW_TIME));
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
