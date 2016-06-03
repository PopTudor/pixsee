package com.marked.pixsee.friendsInvite.commands;

import android.content.Context;
import android.util.Log;

import com.marked.pixsee.commands.ClickCommand;

/**
 * Created by Tudor on 03-Jun-16.
 */
public class ClickAddUser extends ClickCommand {

	public ClickAddUser(Context context) {
		super(context);
	}

	@Override
	public void execute() {
		Log.d("TAG", "execute: ");

	}
}
