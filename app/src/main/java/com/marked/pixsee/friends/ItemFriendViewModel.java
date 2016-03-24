package com.marked.pixsee.friends;

import android.databinding.BaseObservable;

import com.marked.pixsee.data.User;

/**
 * Created by Tudor Pop on 24-Mar-16.
 */
public class ItemFriendViewModel extends BaseObservable {
	private User mUser;

	public ItemFriendViewModel(User user) {
		mUser = user;
	}

	public String getName() {
		return mUser.getName();
	}

	public String getEmail() {
		return mUser.getEmail();
	}

	public String getIcon() {
		return mUser.getEmail();
	}

	public void setUser(User user) {
		mUser = user;
		notifyChange();
	}
}
