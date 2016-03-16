package com.marked.pixsee.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.marked.pixsee.R;
import com.marked.pixsee.friends.data.Friend;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Tudor Pop on 03-Dec-15.
 */
class FriendHolder extends RecyclerView.ViewHolder {
    View itemView;
    private SimpleDraweeView mFriendIconImageView ;
    private TextView mFriendNameTextView, mFriendEmailTextView;

    public FriendHolder(@NotNull View itemView, Context context) {
        super(itemView);
        this.itemView = itemView;
        mFriendIconImageView = (SimpleDraweeView) itemView.findViewById(R.id.friendIconImageView);
        mFriendNameTextView = (TextView) itemView.findViewById(R.id.friendNameTextView);
        mFriendEmailTextView = (TextView) itemView.findViewById(R.id.friendEmail);
    }


    // Define click listener for the ViewHolder's View.
    //            v.setOnClickListener(new View.OnClickListener() {
    //                @Override
    //                public void onClick(View v) {
    //	                Intent intent = new Intent(context, ContactDetailActivity.class);
    //	                context.startActivity(new Intent(context, ContactDetailActivity.class));
    //                }
    //            });


    void bindContact(Friend friend) {
        //        itemView.contactIconImageView.setDefaultImageResId(R.drawable.ic_action_refresh_24dp)
        //        itemView.contactNameTextView.text = contact.name
        //		val uri = Uri.parse(contact);
        //		mFriendIconImageView.setImageURI(uri, context);
        //todo get in parse a contact's profile image
        mFriendNameTextView.setText(friend.getName());
	    mFriendEmailTextView.setText(friend.getEmail());
    }

}