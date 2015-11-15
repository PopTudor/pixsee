package com.marked.vifo.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.marked.vifo.R;
import com.marked.vifo.gcm.extras.IgcmConstants;
import com.marked.vifo.gcm.services.RegistrationIntentService;
import com.marked.vifo.helper.GooglePlusLoginUtils;
import com.marked.vifo.helper.Utils;

import java.io.IOException;

import static com.marked.vifo.gcm.extras.IgcmConstants.REGISTRATION_COMPLETE;
import static com.marked.vifo.gcm.extras.IgcmConstants.USER_REGISTERED;

public class LoginActivity extends AppCompatActivity implements GooglePlusLoginUtils.GPlusLoginStatus, View.OnClickListener {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GooglePlusLoginUtils gLogin;
    private Button mSignInButtonGoogle;
    private Button mSignInButtonPixy;
    private EditText mEmail;
    private EditText mPassword;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSignInButtonGoogle = (Button) findViewById(R.id.signInButtonGoogle);
        mSignInButtonPixy = (Button) findViewById(R.id.signInButtonPixy);
        mEmail = (EditText) findViewById(R.id.emailEditText);
        mPassword = (EditText) findViewById(R.id.passwordEditText);

        // logo svg
        try {
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.container);

            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (25 * scale + 0.5f);

            SVG svg = SVG.getFromResource(this, R.raw.pixie_note_squirrel);
            SVGImageView svgImageView = new SVGImageView(this);
            svgImageView.setSVG(svg);
            svgImageView.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) svgImageView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.setMargins(0, dpAsPixels, 0, 0);

            layout.addView(svgImageView);
        } catch (SVGParseException e) {
            e.printStackTrace();
        }

        gLogin = new GooglePlusLoginUtils(this, mSignInButtonGoogle);
        gLogin.setLoginStatus(this);
        mSignInButtonPixy.setOnClickListener(this);

        // GCM registration
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean sentToken = mSharedPreferences.getBoolean(IgcmConstants.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    String response = intent.getStringExtra("response");
                    if (response.equals("ok")) {
                        mSharedPreferences.edit().putBoolean(IgcmConstants.USER_REGISTERED, true).apply();
                        mProgressDialog.dismiss();
                        Intent intent1 = new Intent(LoginActivity.this, ContactListActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);

                    }
                    Log.d("***", "onReceive " + "Success");
                } else {
                    Log.d("***", "onReceive " + "Error");
                }
            }
        };

        // check if the user has google play services, else finish
        checkPlayServices();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gLogin.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gLogin.connect();
        //        if (ContextCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, permission.CAMERA) != PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, permission.RECORD_AUDIO) != PERMISSION_GRANTED) {
        //            ActivityCompat.requestPermissions(this, new String[]{permission.READ_EXTERNAL_STORAGE, permission.CAMERA, permission.RECORD_AUDIO, permission.WRITE_EXTERNAL_STORAGE}, 100);
        //        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        gLogin.disconnect();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (Utils.isOnline(this)) // if we got internet process the click else tell them to activate it
            switch (v.getId()) {
                case R.id.signInButtonPixy:
                    // Start IntentService to register this application with GCM.
                    intent = new Intent(this, RegistrationIntentService.class);
                    intent.putExtra("email", mEmail.getText().toString());
                    intent.putExtra("password", mPassword.getText().toString());
                    startService(intent);
                    mProgressDialog = ProgressDialog.show(this, "Login", "Please wait...",true);

                    break;
                case R.id.signInButtonGoogle:
                    intent = new Intent(this, ContactListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
            }
        else
            Utils.showNoConnectionDialog(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        // TODO: 08-Nov-15 maybe move the registration from here to Contacts activity so that you register with the server when you want to start a new conversation
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
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
    public void OnSuccessGPlusLogin(Bundle profile) throws IOException {
        String name = profile.getString(GooglePlusLoginUtils.NAME);
        String email = profile.getString(GooglePlusLoginUtils.EMAIL);
        String iconURL = profile.getString(GooglePlusLoginUtils.ICONURL);
        String coverURL = profile.getString(GooglePlusLoginUtils.COVERURL);

        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(USER_REGISTERED, true);
        mEditor.putString(GooglePlusLoginUtils.NAME, name);
        mEditor.putString(GooglePlusLoginUtils.EMAIL, email);
        mEditor.putString(GooglePlusLoginUtils.ICONURL, iconURL);
        mEditor.putString(GooglePlusLoginUtils.COVERURL, coverURL);
        mEditor.apply();
        Log.d("***",
                "OnSuccessGPlusLogin " + name + " " + email + " \n" + iconURL + " \n" + coverURL);
    }

}
