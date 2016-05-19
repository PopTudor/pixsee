package com.marked.pixsee.friends.cards;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;
import com.marked.pixsee.data.message.Message;

import java.util.List;

/**
 * Created by Tudor on 2016-05-19.
 */
public class CardAdapter extends RecyclerView.Adapter<CardVH> {
	private List<Message> mDataset;
	private MessageInteraction messageInteraction;

	public CardAdapter(List<Message> mDataset, MessageInteraction messageInteraction) {
		this.mDataset = mDataset;
		this.messageInteraction = messageInteraction;
	}

	public void setDataset(List<Message> mDataset) {
		this.mDataset.clear();
		this.mDataset.addAll(mDataset);
	}

	@Override
	public CardVH onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
		return new CardVH(view, messageInteraction);
	}

	@Override
	public void onBindViewHolder(CardVH holder, int position) {
		holder.bind(mDataset.get(position));
	}

	@Override
	public int getItemCount() {
		return 0;
	}
	interface MessageInteraction{
		void messageClicked(Message message);

		void moreClicked(Message message);
		void favoriteClicked(Message message);
		void replyClicked(Message message);
	}
}
