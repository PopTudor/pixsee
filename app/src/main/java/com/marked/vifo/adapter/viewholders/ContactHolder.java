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
	private NetworkImageView mContactIconImageView;
	private TextView mContactNameEditText;

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

		mContactIconImageView = (NetworkImageView) v.findViewById(R.id.contactIconImageView);
		mContactNameEditText = (TextView) v.findViewById(R.id.contactNameTextView);
	}

	public void bindContact(Contact contact){
		mContactNameEditText.setText(contact.getName());
		mContactIconImageView.setDefaultImageResId(R.drawable.ic_action_refresh);
	}

}