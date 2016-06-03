package com.marked.pixsee.commands;

import android.content.Context;

/**
 * Created by Tudor Pop on 26-Mar-16.
 */
public abstract class ClickCommand implements Command {
	protected Context mContext;

	public ClickCommand(Context context) {
		mContext = context;
	}


}
