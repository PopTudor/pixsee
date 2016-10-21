package com.marked.pixsee.ui.chat.custom;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.marked.pixsee.R;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.ui.chat.data.MessageConstants;

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
class ImageHolder extends RecyclerView.ViewHolder implements Bindable {
	private Context context;

	private SimpleDraweeView mImage;

	ImageHolder(View itemView, Context context) {
		super(itemView);
		this.context = context;
		this.mImage = (SimpleDraweeView) itemView.findViewById(R.id.imageNetwork);
	}

	@Override
	public void bindMessage(final Message message, final ChatAdapter.ChatInteraction chatInteraction) {
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chatInteraction.chatClicked(v, message, getAdapterPosition());
			}
		});
		StringBuilder path = new StringBuilder(message.getData().get(MessageConstants.DATA_BODY));
		if (message.getMessageType() == MessageConstants.MessageType.ME_IMAGE)
			path.insert(0, UriUtil.LOCAL_FILE_SCHEME+"://");
		final Uri url = UriUtil.parseUriOrNull(path.toString());

		mImage.setImageURI(url, context);
		mImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chatInteraction.imageClicked(v,url);
			}
		});
	}
}
