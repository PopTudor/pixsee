package com.marked.pixsee.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.Pixsee;
import com.marked.pixsee.R;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.login.di.DaggerLoginComponent;
import com.marked.pixsee.login.di.LoginModule;
import com.marked.pixsee.signup.SignUpActivity;
import com.marked.pixsee.utility.DataValidation;
import com.marked.pixsee.utility.Utils;

import javax.inject.Inject;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener,LoginContract.View {
	@Inject
	ProgressDialog mProgressDialog;

	@Inject
	LoginContract.Presenter mPresenter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		checkPlayServices();// check if the user has google play services, else finish

		DaggerLoginComponent.builder()
				.appComponent(((Pixsee) getApplication()).getAppComponent())
				.loginModule(new LoginModule(this))
				.activityModule(new ActivityModule(this))
				.build()
				.inject(this);

		if (BuildConfig.DEBUG) {
			((EditText) findViewById(R.id.emailEditText)).setText("tudor08pop@yahoo.com");
			((EditText) findViewById(R.id.passwordEditText)).setText("password");
		}
		((ImageView) findViewById(R.id.moreImageView)).getDrawable().setColorFilter(ContextCompat.getColor(this,R.color.primary), PorterDuff.Mode.SRC_ATOP);
	}

	public void onClick(View v) {
		// if we got internet, process the click. else tell them to activate it
		if (Utils.isOnline(this))
			switch (v.getId()) {
				case R.id.logInButtonPixy: {
					if (!new DataValidation(this)
							.validate(((EditText) findViewById(R.id.emailEditText)).getText().toString(),
									((EditText)findViewById(R.id.passwordEditText)).getText().toString()))
						return;
					mPresenter.handleLogin(((EditText) findViewById(R.id.emailEditText)).getText().toString(),
							((EditText)findViewById(R.id.passwordEditText)).getText().toString());
					mProgressDialog.show();
					break;
				}
				case R.id.signUpButton: {
					startActivity(new Intent(this, SignUpActivity.class));
					break;
				}
			}
		else
			Utils.showNoConnectionDialog(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mProgressDialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mProgressDialog.cancel();
		mProgressDialog = null;
	}

	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.aboutMenuItem: {
				// TODO: 30-Nov-15 implement this
			}
		}
		return false;
	}

	/*
   * Triggered when the 'more' icon is clicked
   * The icon is in fragment_log_in.xml but handeled here because it won't get triggered in LogInFragment.java
   * */
	public void showPopup(View view) {
		PopupMenu popup = new PopupMenu(this, view);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.menu_popup_login, popup.getMenu());
		popup.setOnMenuItemClickListener(this);
		popup.show();
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
						.getErrorDialog(this, resultCode, LogInActivity.PLAY_SERVICES_RESOLUTION_REQUEST)
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

	@Override
	public void setPresenter(LoginContract.Presenter presenter) {
		mPresenter = presenter;
	}
}