package com.marked.pixsee.friendsInvite.addUsername;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;
import com.marked.pixsee.data.repository.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 03-Jun-16.
 */
public class UsersAdapter extends RecyclerView.Adapter<UserVH> {
	private final List<User> mUsersList;
	private final UserInteraction mInteraction;

	public UsersAdapter(@Nullable UserInteraction userInteraction) {
		mInteraction = userInteraction;
		mUsersList = new ArrayList<>(5);
	}

	@Override
	public UserVH onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_user, parent, false);
		return new UserVH(v,mInteraction);
	}

	public List<User> getUsersList() {
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
		void onClick(User user, int position);
	}
}
