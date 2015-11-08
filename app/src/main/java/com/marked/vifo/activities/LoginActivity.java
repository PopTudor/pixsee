package com.marked.vifo.activities;

import android.Manifest.permission;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.marked.vifo.R;
import com.marked.vifo.extras.Constants;
import com.marked.vifo.extras.GCMPreferences;
import com.marked.vifo.helper.GooglePlusLoginUtils;
import com.marked.vifo.services.RegistrationIntentService;

import java.io.IOException;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LoginActivity extends AppCompatActivity implements GooglePlusLoginUtils.GPlusLoginStatus, View.OnClickListener {
    private GooglePlusLoginUtils gLogin;
    private Button mSignInButtonGoogle;
    private Button mSignInButtonPixy;
    private EditText mName;
    private EditText mEmail;

    private ProgressBar mRegistrationProgressBar;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "LOGIN_ACTIVITY";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView mInformationTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSharedPreferences = getSharedPreferences(getString(R.string.user_data_file_name), MODE_PRIVATE);
        mSignInButtonGoogle = (Button) findViewById(R.id.signInButtonGoogle);
        mSignInButtonPixy = (Button) findViewById(R.id.signInPixy);
        mName = (EditText) findViewById(R.id.nameEditText);
        mEmail = (EditText) findViewById(R.id.emailEditText);

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
//        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
//        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = mSharedPreferences.getBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false);
//                if (sentToken) {
//                    mInformationTextView.setText("Success");
//                } else {
//                    mInformationTextView.setText("Error");
//                }
            }
        };

//        if (checkPlayServices()) {
//            // Start IntentService to register this application with GCM.
//            Intent intent = new Intent(this, RegistrationIntentService.class);
//            startService(intent);
//        }
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
        if (ContextCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, permission.CAMERA) != PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, permission.RECORD_AUDIO) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission.READ_EXTERNAL_STORAGE, permission.CAMERA, permission.RECORD_AUDIO, permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        gLogin.disconnect();
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.signInPixy:
                if (checkPlayServices()) {
                    // Start IntentService to register this application with GCM.
                    intent = new Intent(this, RegistrationIntentService.class);
                    intent.putExtra("name", mName.getText().toString());
                    intent.putExtra("email", mEmail.getText().toString());
                    startService(intent);
                }
//                new Login();
                break;
            case R.id.signInButtonGoogle:
                mEditor = mSharedPreferences.edit();
                mEditor.putBoolean(Constants.LOGGED, true);
                mEditor.apply();
                intent = new Intent(this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;
        }
    }


//    List<NameValuePair> params;
//    private class Login extends AsyncTask<String, String, JSONObject> {
//
//        @Override
//        protected JSONObject doInBackground(String... args) {
//
//            JSONParser json = new JSONParser();
//            params = new ArrayList<>();
//            params.add(new BasicNameValuePair("name", name));
//            params.add(new BasicNameValuePair("mobno", email));
//            params.add((new BasicNameValuePair("reg_id", getString("REG_ID", ""))));
//
//            JSONObject jObj = json.getJSONFromUrl("http://10.0.2.2:8080/login", params);
//            return jObj;
//
//
//        }
//        @Override
//        protected void onPostExecute(JSONObject json) {
//            try {
//                String res = json.getString("response");
//                if(res.equals("Sucessfully Registered")) {
//                    mEditor = mSharedPreferences.edit();
//                    mEditor.putBoolean(Constants.LOGGED, true);
//                    mEditor.apply();
//                    Intent intent = new Intent(LoginActivity.this, ContactListActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                }else{
//                    Toast.makeText(LoginActivity.this, res, Toast.LENGTH_SHORT).show();
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMPreferences.REGISTRATION_COMPLETE));
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
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Intent intent = null;
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {

                } else {
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("We need Contacts permission only if you want to connect with Google or Facebook").setTitle("Why do we ask for this?");
                    // 3. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(LoginActivity.this,new String[]{permission.GET_ACCOUNTS},100);
                        }
                    });
                    dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }
                break;
        }
    }
    @Override
    public void OnSuccessGPlusLogin(Bundle profile) throws IOException {
        String name = profile.getString(GooglePlusLoginUtils.NAME);
        String email = profile.getString(GooglePlusLoginUtils.EMAIL);
        String iconURL = profile.getString(GooglePlusLoginUtils.ICONURL);
        String coverURL = profile.getString(GooglePlusLoginUtils.COVERURL);

        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(Constants.LOGGED, true);
        mEditor.putString(GooglePlusLoginUtils.NAME, name);
        mEditor.putString(GooglePlusLoginUtils.EMAIL, email);
        mEditor.putString(GooglePlusLoginUtils.ICONURL, iconURL);
        mEditor.putString(GooglePlusLoginUtils.COVERURL, coverURL);
        mEditor.apply();
        Log.d("***", "OnSuccessGPlusLogin " + name + " " + email + " \n" + iconURL + " \n" + coverURL);
    }

}
