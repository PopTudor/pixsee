package com.marked.pixsee.ui.friendsInvite.addUsername;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 03-Jun-16.
 */
class UsersAdapter extends RecyclerView.Adapter<UserVH> {
	private final List<Relationship> mUsersList;
	private final UserInteraction mInteraction;

	UsersAdapter(@Nullable UserInteraction userInteraction) {
		mInteraction = userInteraction;
		mUsersList = new ArrayList<>(20); /* constant from the server; the backend returns at most 20 items, friends + users+ userst that
		sent/received friend request */
	}

	@Override
	public UserVH onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_user, parent, false);
		return new UserVH(v, mInteraction);
	}

	List<Relationship> getUsersList() {
		return mUsersList;
	}

	@Override
	public void onBindViewHolder(UserVH holder, int position) {
		holder.bind(mUsersList.get(position));
	}

	@Override
	public int getItemCount() {
		return mUsersList.size();
	}

	interface UserInteraction {
		void onClick(Relationship relationship, int position);
	}
}
