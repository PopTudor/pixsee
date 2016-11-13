package com.marked.pixsee.ui.friendsInvite.addUsername;

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
	private SimpleDraweeView simpleDraweeView;
	private TextView nameTextView;
	private TextView emailTextView;
	private Button addButton;

	UserVH(View itemView, UsersAdapter.UserInteraction mInteraction) {
		super(itemView);
		this.mInteraction = mInteraction;
		simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.iconSimpleDraweeView);
		nameTextView = (TextView) itemView.findViewById(R.id.nameTextview);
		emailTextView = (TextView) itemView.findViewById(R.id.usernameTextview);
		addButton = (Button) itemView.findViewById(R.id.addButton);
	}

	void bind(final User user) {
//		if (user.getIconUrl() != null)
//			simpleDraweeView.setImageURI(Uri.parse(user.getIconUrl()));
		nameTextView.setText(user.getName());
		if (user.getUsername() != null)
			emailTextView.setText(user.getEmail());
		else
			emailTextView.setText(user.getUsername());
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mInteraction.onClick(user, getAdapterPosition());
			}
		});
	}
}
