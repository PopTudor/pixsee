package com.marked.vifo.adapter.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.marked.vifo.R;
import com.marked.vifo.model.Contact;

/**
 * Created by Tudor Pop on 03-Dec-15.
 */
public class ContactHolder extends RecyclerView.ViewHolder {
	private NetworkImageView mFriendIconImageView;
	private TextView mFriendTextView;

	public ContactHolder(View v, final Context context) {
		super(v);
		// Define click listener for the ViewHolder's View.
		//            v.setOnClickListener(new View.OnClickListener() {
		//                @Override
		//                public void onClick(View v) {
		//	                Intent intent = new Intent(context, ContactDetailActivity.class);
		//	                context.startActivity(new Intent(context, ContactDetailActivity.class));
		//                }
		//            });

		mFriendIconImageView = (NetworkImageView) v.findViewById(R.id.contactIconImageView);
		mFriendTextView = (TextView) v.findViewById(R.id.contactNameTextView);
	}

	public void bindContact(Contact contact){
		mFriendTextView.setText(contact.getName());
		mFriendIconImageView.setDefaultImageResId(R.drawable.ic_action_refresh);
	}

}