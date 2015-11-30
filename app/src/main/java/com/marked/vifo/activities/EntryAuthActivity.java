package com.marked.vifo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.marked.vifo.R;

public class EntryAuthActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Button mLogInButton, mSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_auth);
        mLogInButton = (Button) findViewById(R.id.logInButton);
        mSignUp = (Button) findViewById(R.id.signUpButton);

        mLogInButton.setOnClickListener(this);
        mSignUp.setOnClickListener(this);

        checkPlayServices();// check if the user has google play services, else finish
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logInButton:
                startActivity(new Intent(this,LogInActivityy.class));
                break;
            case R.id.signUpButton:
                startActivity(new Intent(this,SignUpActivity.class));
                break;
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
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d("***", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
