package com.marked.pixsee.features.friends.commands;

import android.app.Activity;
import android.content.Context;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.marked.pixsee.commands.ClickCommand;

import javax.inject.Inject;

/**
 * Created by Tudor Pop on 26-Mar-16.
 */
public class InviteCommand extends ClickCommand {
	private Context context;
	private String appLinkUrl;
	private String previewImageUrl;

	/**
	 * Create facebook invite dialog
	 * @param context some context
	 * @param appLinkUrl link to the app
	 * @param previewImageUrl preview image
	 */
	@Inject
	public InviteCommand(Context context,String appLinkUrl,String previewImageUrl) {
		super(context);
		this.context = context;
		this.appLinkUrl = appLinkUrl;
		this.previewImageUrl = previewImageUrl;
	}

	@Override
	public void execute() {
		if (AppInviteDialog.canShow()) {
			AppInviteContent content = new AppInviteContent.Builder()
					                           .setApplinkUrl(appLinkUrl)
					                           .setPreviewImageUrl(previewImageUrl)
					                           .build();
			AppInviteDialog.show((Activity) context, content);
		}
	}
}
