package com.marked.pixsee.frienddetail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;
import com.marked.pixsee.friends.cards.data.Message;
import com.marked.pixsee.friends.cards.data.MessageConstants;
import com.marked.pixsee.frienddetail.viewholders.ImageHolder;
import com.marked.pixsee.frienddetail.viewholders.MessageHolder;
import com.marked.pixsee.frienddetail.viewholders.TypingHolder;

import java.util.List;


/**
 * Created by Tudor Pop on 04-Dec-15.
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private List<Message> dataset;

	public MessageAdapter(List<Message> dataset) {
		this.dataset = dataset;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// Create a new view.
		View v;
		switch (viewType) {
			case MessageConstants.MessageType.ME_MESSAGE :
				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_mine_text, parent, false);
				return new MessageHolder(v, parent.getContext());

			case MessageConstants.MessageType.YOU_MESSAGE :
				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_other_text, parent, false);
				return new MessageHolder(v, parent.getContext());

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
				return new MessageHolder(v, parent.getContext());

		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof MessageHolder){
			((MessageHolder)holder).bindMessage(dataset.get(position));
		}else if (holder instanceof ImageHolder){
			((ImageHolder)holder).bindMessage(dataset.get(position));
		}else if (holder instanceof TypingHolder){
			((TypingHolder)holder).start();
		}
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

	/** If the message has the flag MessageConstants.MessageType.ME_MESSAGE then the message will be printed to the right
	 * else if the message has the flag MessageConstants.MessageType.YOU_MESSAGE then the message will be positioned to the left
	 * or if the MessageConstants.MessageType.Typing then the message will be positioned on the left as in You are typing and
	 * I wait for you to finish
	 * @param position
	 * @return
	 */
	@Override
	public int getItemViewType(int position) {
		return dataset.get(position).getMessageType();
	}

	@Override
	public int getItemCount() {
		return dataset.size();
	}
}
