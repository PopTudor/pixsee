package com.marked.pixsee.friends.commands;

import android.content.Context;

import com.marked.pixsee.commands.ClickCommand;
import com.marked.pixsee.commands.Command;

import javax.inject.Inject;

/**
 * Created by Tudor Pop on 26-Mar-16.
 */
public class OpenCameraCommand extends ClickCommand implements Command {
	@Inject
	public OpenCameraCommand(Context context) {
		super(context);
	}

	@Override
	public void execute() {
//		Intent intent = new Intent(mContext, ImageTargets.class);
//		intent.putExtra("ACTIVITY_TO_LAUNCH", "app.ImageTargets.ImageTargets");
//		mContext.startActivity(intent);
	}
}
