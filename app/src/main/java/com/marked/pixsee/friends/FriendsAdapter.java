package com.marked.pixsee.friends;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;
import com.marked.pixsee.data.User;
import com.marked.pixsee.databinding.ItemFriendBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.common.internal.Preconditions.checkNotNull;


/**
 * Created by Tudor Pop on 30-Nov-15.
 */
class FriendsAdapter extends RecyclerView.Adapter<FriendVH> {
	private List<User> mDataSet;
	private FriendFragment.FriendItemListener friendItemListener;

	FriendsAdapter(@NotNull FriendFragment.FriendItemListener friendItemListener) {
		this.friendItemListener = checkNotNull(friendItemListener);
		mDataSet=new ArrayList<User>();
	}

	@Override
	public FriendVH onCreateViewHolder(ViewGroup parent, int viewType) {
		ItemFriendBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_friend, parent, false);
		return new FriendVH(binding);
	}

	public void setDataSet(List<User> dataSet) {
		mDataSet = dataSet;
	}

	@Override
	public void onBindViewHolder(FriendVH holder, int position) {
		final User friend = mDataSet.get(position);
		holder.bindContact(friend);
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// itemView is a public member that points to the root element of the contactHolder (in the layout)
				// TODO: 03-Dec-15 send contact detail from here to ContactDetailActivity
				// send the clicked contact to detail activity
				friendItemListener.onFriendClick(friend);
			}
		});
	}

	// Return the size of your mDataSet (invoked by the layout manager)

	@Override
	public int getItemCount() {
		return mDataSet.size();
	}
}
