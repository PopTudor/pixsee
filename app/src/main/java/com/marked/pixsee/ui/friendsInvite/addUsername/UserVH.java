package com.marked.pixsee.ui.friendsInvite.addUsername;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.marked.pixsee.R;
import com.marked.pixsee.data.user.User;

/**
 * Created by Tudor on 03-Jun-16.
 */
class UserVH extends RecyclerView.ViewHolder {
	private final UsersAdapter.UserInteraction mInteraction;
	private final Button addedButton;
	private final Button addButton;
	private final SimpleDraweeView simpleDraweeView;
	private final TextView nameTextView;
	private final TextView usernameTextView;
	private final Context mContext;

	UserVH(View itemView, UsersAdapter.UserInteraction mInteraction) {
		super(itemView);
		this.mInteraction = mInteraction;
		mContext = itemView.getContext();
		simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.iconSimpleDraweeView);
		nameTextView = (TextView) itemView.findViewById(R.id.nameTextview);
		usernameTextView = (TextView) itemView.findViewById(R.id.usernameTextview);
		addButton = (Button) itemView.findViewById(R.id.addButton);
		addedButton = (Button) itemView.findViewById(R.id.addedButton);
	}

	void bind(final Relationship relationship) {
		final User user = relationship.user;
//		if (user.getIconUrl() != null)
//			simpleDraweeView.setImageURI(Uri.parse(user.getIconUrl()));

		nameTextView.setText(user.getName());
		usernameTextView.setText(user.getUsername());

		setupAddButton(relationship.status);

		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mInteraction.onClick(relationship, getAdapterPosition());
			}
		});
	}

	private void setupAddButton(String status) {
		if (status.equals(Relationship.Status.SENT_FRIEND_REQUEST) || status.equals(Relationship.Status.FRIEND)) {
			addButton.setVisibility(View.GONE);
			addedButton.setVisibility(View.VISIBLE);
		} else {
			addButton.setVisibility(View.VISIBLE);
			addedButton.setVisibility(View.GONE);
		}
	}
}
