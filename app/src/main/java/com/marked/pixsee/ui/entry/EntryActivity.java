package com.marked.pixsee.ui.entry;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.marked.pixsee.R;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.ui.authentification.AuthenticationActivity;
import com.marked.pixsee.ui.main.MainActivity;
import com.marked.pixsee.utility.GCMConstants;

import bolts.AppLinks;


public class EntryActivity extends AppCompatActivity {
	private boolean mUserRegistered;
	private Intent whatToStartIntent;

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
		if (mUserRegistered) {
			whatToStartIntent = new Intent(this, MainActivity.class);
			whatToStartIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		} else {
			whatToStartIntent = new Intent(this, AuthenticationActivity.class);
		}
		prepareFriendRequest();
		startActivity(whatToStartIntent);
		finish();
	}

	private void prepareFriendRequest() {
		String action = getIntent().getAction();
		if (action!=null && action.equals(getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION))) {
			whatToStartIntent.putExtra(getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION), new User(getIntent().getExtras()));
		}
	}
}
