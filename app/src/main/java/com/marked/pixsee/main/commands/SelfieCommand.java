package com.marked.pixsee.main.commands;

import android.content.Context;
import android.content.Intent;

import com.marked.pixsee.commands.ClickCommand;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.face.SelfieActivity;

/**
 * Created by Tudor Pop on 26-Mar-16.
 */
public class SelfieCommand extends ClickCommand implements Command {
	public SelfieCommand(Context context) {
		super(context);
	}

	@Override
	public void execute() {
		Intent intent = new Intent(mContext, SelfieActivity.class);
		mContext.startActivity(intent);
	}
}
