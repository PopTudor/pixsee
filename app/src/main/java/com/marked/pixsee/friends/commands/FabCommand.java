package com.marked.pixsee.friends.commands;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.marked.pixsee.commands.ClickCommand;
import com.marked.pixsee.friends.FriendFragment;

import javax.inject.Inject;

/**
 * Created by Tudor Pop on 26-Mar-16.
 */
public class FabCommand extends ClickCommand {

	@Inject
	public FabCommand(Context context) {
		super(context);
	}

	@Override
	public void execute() {
		Intent intent = new AppInviteInvitation.IntentBuilder("Invite more friends").setMessage("Check out this cool app !")
		                                                                            //.setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
		                                                                            //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
		                                                                            //.setCallToActionText(getString(R.string.invitation_cta))
		                                                                            .build();
		((Activity) mContext).startActivityForResult(intent, FriendFragment.REQUEST_INVITE);
	}
}
