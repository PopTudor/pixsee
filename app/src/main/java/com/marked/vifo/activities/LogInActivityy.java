package com.marked.vifo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.marked.vifo.R;
import com.marked.vifo.extras.IHTTPStatusCodes;
import com.marked.vifo.gcm.RegistrationBroadcastReceiver;
import com.marked.vifo.gcm.extras.IgcmConstants;
import com.marked.vifo.gcm.services.LogInRegistrationIntentService;
import com.marked.vifo.helper.Utils;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LogInActivityy extends AppCompatActivity implements View.OnClickListener ,PopupMenu.OnMenuItemClickListener{
    private Button mLogInButtonPixy;
    private MaterialEditText mEmail, mPassword;
    private RegistrationBroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout mContainer;
    private ImageView mMoreImageView;
    private LocalBroadcastManager mBroadcastManagerastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mLogInButtonPixy = (Button) findViewById(R.id.logInButtonPixy);
        mEmail = (MaterialEditText) findViewById(R.id.emailEditText);
        mPassword = (MaterialEditText) findViewById(R.id.passwordEditText);
        mContainer = (CoordinatorLayout) findViewById(R.id.container_log_in_fragment);
        mMoreImageView = (ImageView) findViewById(R.id.moreImageView);

        mMoreImageView.setColorFilter(ContextCompat.getColor(this, R.color.primary));

        mBroadcastManagerastManager = LocalBroadcastManager.getInstance(this);

        // GCM registration
        mRegistrationBroadcastReceiver = new RegistrationBroadcastReceiver();
        mRegistrationBroadcastReceiver.setOnRegistrationBroadcastReceiverListener(new RegistrationBroadcastReceiver.RegistrationBroadcastReceiverListener() {
            @Override
            public void onDismiss() {
                mProgressDialog.dismiss();
            }

            @Override
            public void onError(int errorStatusCode) {
                Snackbar snackbar = null;
                switch (errorStatusCode) {
                    case IHTTPStatusCodes.REQUEST_CONFLICT:
                        snackbar = Utils.createWhiteSnackBar(LogInActivityy.this, mContainer, "You already have an account");
                        break;
                    case IHTTPStatusCodes.REQUEST_TIMEOUT:
                        snackbar = Utils.createWhiteSnackBar(LogInActivityy.this, mContainer, "Timeout error");
                        break;
                    case IHTTPStatusCodes.REQUEST_INCORRECT_PASSWORD:
                        snackbar = Utils.createWhiteSnackBar(LogInActivityy.this, mContainer, "Incorrect password");
                        break;
                    case IHTTPStatusCodes.NOT_FOUND:
                        snackbar = Utils.createWhiteSnackBar(LogInActivityy.this, mContainer, "We are sorry, but we did not found you");
                        break;
                }
                if (snackbar != null) {
                    snackbar.getView().setBackgroundColor(Color.WHITE);
                    snackbar.show();
                }
            }
        });

    }



    @Override
    public void onResume() {
        super.onResume();
        mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(IgcmConstants.ACTION_LOGIN));
        mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(IgcmConstants.ACTION_ERROR));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (Utils.isOnline(this)) // if we got internet, process the click. else tell them to activate it
            switch (v.getId()) {
                case R.id.logInButtonPixy:
                    if (!Utils.checkEnteredData(this, mEmail.getText().toString(), mPassword.getText().toString(), mContainer))
                        break;
                    LogInRegistrationIntentService.startActionLogin(this, mEmail.getText().toString(), mPassword.getText().toString());
                    mProgressDialog = ProgressDialog.show(this, "Login", "Please wait ...", true);
                    break;
                case R.id.signUpButton:
                    startActivity(new Intent(this,SignUpActivity.class));
                    break;
            }
        else
            Utils.showNoConnectionDialog(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutMenuItem:
                // TODO: 30-Nov-15 implement this
                break;
        }
        return false;
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
}
