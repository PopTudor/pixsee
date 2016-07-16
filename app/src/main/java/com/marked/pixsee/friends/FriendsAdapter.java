package com.marked.pixsee.friends;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;
import com.marked.pixsee.data.user.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.common.internal.Preconditions.checkNotNull;


/**
 * Created by Tudor Pop on 30-Nov-15.
 */
class FriendsAdapter extends RecyclerView.Adapter<FriendVH> {
	private final List<User> mDataSet;
	private FriendFragment.FriendFragmentInteractionListener friendFragmentInteractionListener;

	FriendsAdapter(@NotNull FriendFragment.FriendFragmentInteractionListener friendFragmentInteractionListener) {
		this.friendFragmentInteractionListener = checkNotNull(friendFragmentInteractionListener);
		mDataSet= new ArrayList<>();
	}

	@Override
	public FriendVH onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

		return new FriendVH(v,friendFragmentInteractionListener);
	}

	public void setDataSet(List<User> dataSet) {
		mDataSet.clear();
		mDataSet.addAll(dataSet);
	}

	@Override
	public void onBindViewHolder(FriendVH holder, int position) {
		final User friend = mDataSet.get(position);
		holder.bindFriend(friend);
	}

	// Return the size of your mDataSet (invoked by the layout manager)

	@Override
	public int getItemCount() {
		return mDataSet.size();
	}
}
