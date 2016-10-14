package com.marked.pixsee.features.friends;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.marked.pixsee.R;
import com.marked.pixsee.data.user.User;

/**
 * Created by Tudor Pop on 03-Dec-15.
 */
class FriendVH extends RecyclerView.ViewHolder {
	private static FriendClick FRIEND_CLICK;
	private SimpleDraweeView simpleDraweeView;
	private TextView friendNameTextView;
	private TextView friendBodyTextView;
	private FriendFragment.FriendFragmentInteractionListener friendFragmentInteractionListener;

	public FriendVH(@NonNull View binding, FriendFragment.FriendFragmentInteractionListener interactionListener) {
		super(binding);
		simpleDraweeView = (SimpleDraweeView) binding.findViewById(R.id.friendIconImageView);
		friendNameTextView = (TextView) binding.findViewById(R.id.friendNameTextView);
		friendBodyTextView = (TextView) binding.findViewById(R.id.friendBodyTextView);
		friendFragmentInteractionListener = interactionListener;

	}


	// Define click listener for the ViewHolder's View.
	//            v.setOnClickListener(new View.OnClickListener() {
	//                @Override
	//                public void onClick(View v) {
	//	                Intent intent = new Intent(context, ContactDetailActivity.class);
	//	                context.startActivity(new Intent(context, ContactDetailActivity.class));
	//                }
	//            });


	public void bindFriend(final User friend) {
		//		val uri = Uri.parse(contact);
		//		mFriendIconImageView.setImageURI(uri, context);
		//todo get in parse a contact's profile image
		FRIEND_CLICK = new FriendClick(friend);
		if (friend.getIconUrl() != null)
			simpleDraweeView.setImageURI(Uri.parse(friend.getIconUrl()));
		friendNameTextView.setText(friend.getName());
		itemView.setOnClickListener(FRIEND_CLICK); // // TODO: 31.05.2016 how can this be done with a single line of code ?
		friendNameTextView.setOnClickListener(FRIEND_CLICK);
	}
	private class FriendClick implements View.OnClickListener{
		private User friend;

		public FriendClick(User friend) {
			this.friend = friend;
		}

		@Override
		public void onClick(View v) {
			friendFragmentInteractionListener.onFriendClick(friend);
		}
	}

}