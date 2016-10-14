package com.marked.pixsee.features.friendsInvite.commands;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.commands.ClickCommand;
import com.marked.pixsee.features.friendsInvite.addUsername.AddUsernameFragment;

/**
 * Created by Tudor on 03-Jun-16.
 */
public class ClickAddUser extends ClickCommand {
	private int fragmentContainer;
	private Fragment fragment;

	public ClickAddUser(Context context, int fragmentContainer, AddUsernameFragment usernameFragment) {
		super(context);
		this.fragmentContainer = fragmentContainer;
		this.fragment = usernameFragment;
	}

	@Override
	public void execute() {
		FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();
		manager.beginTransaction().replace(fragmentContainer,fragment).addToBackStack(null).commit();
	}
}
