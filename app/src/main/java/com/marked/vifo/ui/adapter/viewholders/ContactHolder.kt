package com.marked.vifo.ui.adapter.viewholders;

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.marked.vifo.R
import com.marked.vifo.model.contact.Contact

/**
 * Created by Tudor Pop on 03-Dec-15.
 */
class ContactHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

	private var mFriendIconImageView = itemView.findViewById(R.id.contactIconImageView) as SimpleDraweeView
	private var mFriendTextView = itemView.findViewById(R.id.contactNameTextView) as TextView

	// Define click listener for the ViewHolder's View.
	//            v.setOnClickListener(new View.OnClickListener() {
	//                @Override
	//                public void onClick(View v) {
	//	                Intent intent = new Intent(context, ContactDetailActivity.class);
	//	                context.startActivity(new Intent(context, ContactDetailActivity.class));
	//                }
	//            });


	fun bindContact(contact: Contact) {
		//        itemView.contactIconImageView.setDefaultImageResId(R.drawable.ic_action_refresh_24dp)
		//        itemView.contactNameTextView.text = contact.name
//		val uri = Uri.parse(contact);
//		mFriendIconImageView.setImageURI(uri, context);
		//todo get in parse a contact's profile image
		mFriendTextView.text = contact.name;
	}

}