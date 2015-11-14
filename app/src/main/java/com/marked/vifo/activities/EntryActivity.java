package com.marked.vifo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.marked.vifo.R;
import com.marked.vifo.extras.GCMConstants;


public class EntryActivity extends AppCompatActivity {
	private boolean user_registered;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.user_data_file_name), MODE_PRIVATE);
		user_registered = mSharedPreferences.getBoolean(GCMConstants.USER_REGISTERED, false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent;
		if (user_registered) {
			intent = new Intent(this, ContactListActivity.class);
		} else {
			intent = new Intent(this, LoginActivity.class);
		}
		startActivity(intent);
        finish();
	}

}
