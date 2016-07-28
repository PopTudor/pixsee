package com.marked.pixsee.entry;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.marked.pixsee.R;
import com.marked.pixsee.authentification.AuthenticationActivity;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.main.MainActivity;
import com.marked.pixsee.utility.GCMConstants;

import bolts.AppLinks;


public class EntryActivity extends AppCompatActivity {
	private boolean mUserRegistered;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserRegistered = PreferenceManager.getDefaultSharedPreferences(this)
		                                   .getBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, false);
		Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
		if (targetUrl != null) {
			Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
		}
	}

	public void onResume() {
		super.onResume();
		Intent intent;
		if (mUserRegistered) {
			intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		} else {
			intent = new Intent(this, AuthenticationActivity.class);
		}
		/* the intent was sent from FCM for a friend request, add the following extras */
		if (getIntent().getAction() != null && getIntent().getAction().equals(getString(R.string.FRIEND_REQUEST))) {
			User user = getIntent().getParcelableExtra(User.TAG);
			intent.putExtra(MessageConstants.MESSAGE_TYPE, MessageConstants.MessageType.FRIEND_REQUEST);
			intent.putExtra(DatabaseContract.AppsUser.TABLE_NAME, user);
		}
		startActivity(intent);
		finish();
	}
}
