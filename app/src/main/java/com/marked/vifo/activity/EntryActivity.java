package com.marked.vifo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.marked.vifo.extra.GCMConstants;


public class EntryActivity extends AppCompatActivity {
	private boolean mUserRegistered;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mUserRegistered = mSharedPreferences.getBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent;
		if (mUserRegistered) {
			intent = new Intent(this, ContactListActivity.class);
		} else {
			intent = new Intent(this, EntryAuthActivity.class);
		}
		startActivity(intent);
        finish();
	}

}
