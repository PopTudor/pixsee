package com.marked.pixsee.face.commands;

import android.content.Context;
import android.content.Intent;

import com.marked.pixsee.commands.ClickCommand;
import com.marked.pixsee.store.ShopActivity;

/**
 * Created by Tudor on 2016-05-10.
 */
public class ShopImageButtonClick extends ClickCommand {

	public ShopImageButtonClick(Context context) {
		super(context);
	}

	@Override
	public void execute() {
		Intent intent = new Intent(mContext, ShopActivity.class);
		mContext.startActivity(intent);
	}
}
