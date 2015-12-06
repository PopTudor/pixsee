package com.marked.vifo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.vifo.R;
import com.marked.vifo.adapter.viewholders.MessageHolder;
import com.marked.vifo.model.Message;

import java.util.List;

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {
	List<Message> mDataSet;
	Context mContext;

	public MessageAdapter(Context context, List<Message> dataSet) {
		mContext = context;
		mDataSet = dataSet;
	}

	@Override
	public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// Create a new view.
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_item, parent, false);
		return new MessageHolder(v, mContext);
	}

	@Override
	public void onBindViewHolder(MessageHolder holder, int position) {
		Message contact = mDataSet.get(position);
		holder.bindContact(contact);
	}

	@Override
	public int getItemCount() {
		return mDataSet.size();
	}
}
