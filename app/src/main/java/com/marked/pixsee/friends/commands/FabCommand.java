package com.marked.pixsee.friends.commands;

import android.app.Activity;
import android.content.Context;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.marked.pixsee.commands.ClickCommand;

import javax.inject.Inject;

/**
 * Created by Tudor Pop on 26-Mar-16.
 */
public class FabCommand extends ClickCommand {
	Context context;
	@Inject
	public FabCommand(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public void execute() {
		String appLinkUrl, previewImageUrl;

		appLinkUrl = "https://fb.me/1707374479479383";
		previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";

		if (AppInviteDialog.canShow()) {
			AppInviteContent content = new AppInviteContent.Builder()
					                           .setApplinkUrl(appLinkUrl)
					                           .setPreviewImageUrl(previewImageUrl)
					                           .build();
			AppInviteDialog.show((Activity) context, content);
		}
	}
}
