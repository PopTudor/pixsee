package com.marked.pixsee.chat.custom;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.marked.pixsee.R;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.data.MessageConstants;

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
public class ImageHolder extends RecyclerView.ViewHolder {
	private Context context;

	private SimpleDraweeView mImage;

	public ImageHolder(View itemView, Context context) {
		super(itemView);
		this.context = context;
		this.mImage = (SimpleDraweeView) itemView.findViewById(R.id.imageNetwork);
	}

	public void bindMessage(final Message message, final ChatAdapter.ChatInteraction chatInteraction) {
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chatInteraction.chatClicked(v, message, getAdapterPosition());
			}
		});
		String scheme = UriUtil.LOCAL_FILE_SCHEME;
		String path = message.getData().get(MessageConstants.DATA_BODY)
//				.replace(ServerConstants.SCHEME_HTTP, "")
//				.replace(ServerConstants.PORT, "/")
				;
		if (message.getMessageType() == MessageConstants.MessageType.YOU_IMAGE) {
			scheme = UriUtil.HTTP_SCHEME;
		}
		final Uri url = UriUtil.parseUriOrNull(path);

		mImage.setImageURI(url, context);
		mImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chatInteraction.imageClicked(v,url);
			}
		});
	}
}
