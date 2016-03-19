package com.marked.pixsee.entry;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.friends.FriendsActivity;
import com.marked.pixsee.utility.GCMConstants;


public class EntryActivity extends AppCompatActivity {
	private boolean mUserRegistered;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserRegistered = PreferenceManager.getDefaultSharedPreferences(this)
		                                   .getBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, false);
	}

	public void onResume() {
		super.onResume();
		Intent intent = new Intent();
		if (mUserRegistered) {
			intent = new Intent(this, FriendsActivity.class);
		} else {
			intent = new Intent(this, AuthActivity.class);
		}
		startActivity(intent);
		finish();
	}
}
