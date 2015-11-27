package com.marked.vifo.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.marked.vifo.R;
import com.marked.vifo.fragments.LogInFragment;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LogInFragment.LogInInteractionFragmentListener, PopupMenu.OnMenuItemClickListener {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Button mLogInButton, mSignUp;
    private FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLogInButton = (Button) findViewById(R.id.logInButton);
        mSignUp = (Button) findViewById(R.id.signUpButton);

        mLogInButton.setOnClickListener(this);
        mSignUp.setOnClickListener(this);

        mFragmentManager = getSupportFragmentManager();

        checkPlayServices();// check if the user has google play services, else finish
    }
    /*
    * Triggered when the 'more' icon is clicked
    * The icon is in fragment_log_in.xml but handeled here because it won't get triggered in LogInFragment.java
    * */
    public void showPopup(View view){
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup_login, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logInButton:
                mFragmentManager.beginTransaction().add(R.id.fragmentContainer, LogInFragment.newInstance()).addToBackStack(null).commit();
                break;
            case R.id.signUpButton:

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutMenuItem:

                break;
        }
        return false;
    }
}
