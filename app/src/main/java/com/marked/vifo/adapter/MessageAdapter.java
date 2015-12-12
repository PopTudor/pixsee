package com.marked.vifo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.marked.vifo.R;
import com.marked.vifo.adapter.viewholders.MessageHolder;
import com.marked.vifo.extra.MessageConstants;
import com.marked.vifo.model.Message;

import java.util.List;

import static android.view.LayoutInflater.from;

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
		View v = null;
		switch (viewType) {
			case MessageConstants.MessageType.ME:
				v = from(parent.getContext()).inflate(R.layout.message_layout_item_right, parent, false);
				break;
			case MessageConstants.MessageType.YOU:
				v = from(parent.getContext()).inflate(R.layout.message_layout_item_left, parent, false);
				break;
		}
		return new MessageHolder(v, mContext);
	}

	@Override
	public void onBindViewHolder(MessageHolder holder, int position) {
		Message contact = mDataSet.get(position);
		holder.bindContact(contact);
	}

	@Override
	public int getItemViewType(int position) {

		return mDataSet.get(position).getMessageType();
	}

	@Override
	public int getItemCount() {
		return mDataSet.size();
	}

}
