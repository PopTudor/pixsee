package com.marked.pixsee.friends;

import android.support.v7.widget.RecyclerView;

import com.marked.pixsee.data.User;
import com.marked.pixsee.databinding.ItemFriendBinding;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Tudor Pop on 03-Dec-15.
 */
class FriendHolder extends RecyclerView.ViewHolder {
	public ItemFriendBinding mBinding;

	public FriendHolder(@NotNull ItemFriendBinding binding) {
		super(binding.rootItemFriend);
		this.mBinding = binding;
	}


	// Define click listener for the ViewHolder's View.
	//            v.setOnClickListener(new View.OnClickListener() {
	//                @Override
	//                public void onClick(View v) {
	//	                Intent intent = new Intent(context, ContactDetailActivity.class);
	//	                context.startActivity(new Intent(context, ContactDetailActivity.class));
	//                }
	//            });


	void bindContact(User friend) {
		//		val uri = Uri.parse(contact);
		//		mFriendIconImageView.setImageURI(uri, context);
		//todo get in parse a contact's profile image
		if (mBinding.getViewModel() == null)
			mBinding.setViewModel(new ItemFriendViewModel(friend));
		else
			mBinding.getViewModel().setUser(friend);
	}

}