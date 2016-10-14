package com.marked.pixsee.features.friendsInvite.commands;

import android.content.Context;
import android.content.Intent;

import com.marked.pixsee.commands.ClickCommand;
import com.marked.pixsee.data.user.User;

/**
 * Created by Tudor on 03-Jun-16.
 */
public class ClickShareUsername extends ClickCommand {

	private User mAppsUser;

	public ClickShareUsername(Context context,User appsUser) {
		super(context);
		mAppsUser = appsUser;
	}

	@Override
	public void execute() {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Add me on Pixsee! Username: "+ mAppsUser.getUsername());
		mContext.startActivity(Intent.createChooser(sharingIntent, "Invite friends"));
	}
}
