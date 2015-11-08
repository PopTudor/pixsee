package com.marked.vifo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.marked.vifo.R;
import com.marked.vifo.extras.Constants;


public class EntryActivity extends AppCompatActivity {
	private boolean logged;
    private SharedPreferences mSharedPreferences;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences preferences = getSharedPreferences(getString(R.string.user_data_file_name), MODE_PRIVATE);
		logged = preferences.getBoolean(Constants.LOGGED, false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent;
		if (logged) {
			intent = new Intent(this, ContactListActivity.class);
		} else {
			intent = new Intent(this, LoginActivity.class);
		}
		startActivity(intent);
        finish();
	}

}
