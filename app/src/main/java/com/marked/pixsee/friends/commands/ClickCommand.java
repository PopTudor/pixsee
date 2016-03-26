package com.marked.pixsee.friends.commands;

import android.content.Context;
import android.view.View;

/**
 * Created by Tudor Pop on 26-Mar-16.
 */
public abstract class ClickCommand implements Command,View.OnClickListener {
	Context mContext;

	public ClickCommand(Context context) {
		mContext = context;
	}

	@Override
	public void onClick(View v) {
		execute();
	}
}
