package com.marked.pixsee.entry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.marked.pixsee.R;
import com.marked.pixsee.login.LogInActivity;
import com.marked.pixsee.signup.SignUpActivity;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
	Button entryLogInButton, entrySignUpButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry_auth);

		entryLogInButton = (Button) findViewById(R.id.entryLogInButton);
		entrySignUpButton = (Button) findViewById(R.id.entrySignUpButton);

		entryLogInButton.setOnClickListener(this);
		entrySignUpButton.setOnClickListener(this);

		checkPlayServices();// check if the user has google play services, else finish
	}


	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.entryLogInButton:
				startActivity(new Intent(this, LogInActivity.class));
				break;
			case R.id.entrySignUpButton:
				startActivity(new Intent(this, SignUpActivity.class));
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability
						.getErrorDialog(this, resultCode, AuthActivity.PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.d("***", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

}
