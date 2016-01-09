package com.marked.vifo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.marked.vifo.extra.GCMConstants;

import io.fabric.sdk.android.Fabric;


public class EntryActivity extends AppCompatActivity {
	private boolean mUserRegistered;
	Fabric fabric = null;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    fabric = new Fabric.Builder(this)
			    .kits(new Crashlytics())
			    .debuggable(true) // TODO: 13-Dec-15 disable this
			    .build();
	    Fabric.with(fabric);

		mUserRegistered =  PreferenceManager.getDefaultSharedPreferences(this).getBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, false);
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
