package com.marked.pixsee.friends;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.marked.pixsee.R;
import com.marked.pixsee.friends.data.User;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Tudor Pop on 03-Dec-15.
 */
class FriendVH extends RecyclerView.ViewHolder {
	private SimpleDraweeView simpleDraweeView;
	private TextView friendNameTextView;
	private TextView friendBodyTextView;

	public FriendVH(@NotNull View binding) {
		super(binding);
		simpleDraweeView = (SimpleDraweeView) binding.findViewById(R.id.friendIconImageView);
		friendNameTextView = (TextView) binding.findViewById(R.id.friendNameTextView);
		friendBodyTextView = (TextView) binding.findViewById(R.id.friendBodyTextView);
	}


	// Define click listener for the ViewHolder's View.
	//            v.setOnClickListener(new View.OnClickListener() {
	//                @Override
	//                public void onClick(View v) {
	//	                Intent intent = new Intent(context, ContactDetailActivity.class);
	//	                context.startActivity(new Intent(context, ContactDetailActivity.class));
	//                }
	//            });


	public void bindFriend(User friend) {
		//		val uri = Uri.parse(contact);
		//		mFriendIconImageView.setImageURI(uri, context);
		//todo get in parse a contact's profile image
		if (friend.getIcon() != null)
			simpleDraweeView.setImageURI(Uri.parse(friend.getIcon()));
		friendNameTextView.setText(friend.getName());
		friendBodyTextView.setText(friend.getLastMessage());
	}

}