package com.marked.pixsee.chat.custom;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.data.MessageConstants;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tudor Pop on 04-Dec-15.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private final List<Message> dataset;
	private final ChatInteraction chatInteraction;

	public ChatAdapter(ChatInteraction chatInteraction) {
		this.dataset = new ArrayList<>();
		this.chatInteraction = chatInteraction;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// Create a new view.
		View v;
		switch (viewType) {
			case MessageConstants.MessageType.ME_MESSAGE :
				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_mine_text, parent, false);
				return new MessageHolder(v);

			case MessageConstants.MessageType.YOU_MESSAGE :
				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_other_text, parent, false);
				return new MessageHolder(v);

			case MessageConstants.MessageType.ME_IMAGE :
				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_mine_image, parent, false);
				return new ImageHolder(v, parent.getContext());

			case MessageConstants.MessageType.YOU_IMAGE :
				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_other_image, parent, false);
				return new ImageHolder(v, parent.getContext());

			case MessageConstants.MessageType.TYPING :
				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_typing, parent, false);
				return new TypingHolder(v);

			default:
				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_other_text, parent, false);
				return new MessageHolder(v);

		}
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
		((Bindable) holder).bindMessage(dataset.get(position), chatInteraction);
	}

	@Override
	public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
		super.onViewDetachedFromWindow(holder);
		if (holder instanceof TypingHolder) {
			/* must call stop and set recyclable to false because else will cache the 'typing animation' and it will not stop or it will block*/
			((TypingHolder)holder).stop();
			holder.setIsRecyclable(false);
		} else if (holder!=null)
			holder.setIsRecyclable(true);
	}

	@Override
	public int getItemViewType(int position) {
		return dataset.get(position).getMessageType();
	}

	@Override
	public int getItemCount() {
		return dataset.size();
	}

	public List<Message> getDataset() {
		return dataset;
	}

	public interface ChatInteraction {
		void chatClicked(View view,Message message,int position);

		void imageClicked(View view, Uri uri);
	}
}
