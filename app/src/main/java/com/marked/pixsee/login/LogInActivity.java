package com.marked.pixsee.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.R;
import com.marked.pixsee.di.components.DaggerActivityComponent;
import com.marked.pixsee.di.modules.ActivityModule;
import com.marked.pixsee.service.LogInRegistrationIntentService;
import com.marked.pixsee.service.RegistrationBroadcastReceiver;
import com.marked.pixsee.signup.SignUpActivity;
import com.marked.pixsee.utility.DataValidation;
import com.marked.pixsee.utility.GCMConstants;
import com.marked.pixsee.utility.Utils;

import javax.inject.Inject;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
	@Inject
	ProgressDialog mProgressDialog;
	@Inject
	RegistrationBroadcastReceiver mRegistrationBroadcastReceiver;
	@Inject
	LocalBroadcastManager mBroadcastManagerastManager;

	EditText emailEditText, passwordEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		DaggerActivityComponent.builder().activityModule(new ActivityModule(this)).build()
		                       .inject(this);

		emailEditText = (EditText) findViewById(R.id.emailEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);

		if (BuildConfig.DEBUG) {
			emailEditText.setText("tudor08pop@yahoo.com");
			passwordEditText.setText("parola");
		}
	}

	public void onResume() {
		super.onResume();
		mBroadcastManagerastManager
				.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMConstants.ACTION_LOGIN));
		mBroadcastManagerastManager
				.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMConstants.ACTION_ERROR));
	}

	public void onPause() {
		mBroadcastManagerastManager.unregisterReceiver(mRegistrationBroadcastReceiver);
		super.onPause();
	}

	public void onClick(View v) {
		// if we got internet, process the click. else tell them to activate it
		if (Utils.INSTANCE.isOnline(this))
			switch (v.getId()) {
				case R.id.logInButtonPixy: {
					if (!new DataValidation(this)
							.validate(emailEditText.getText().toString(), passwordEditText.getText()
							                                                              .toString()))
						return;
					LogInRegistrationIntentService.startActionLogin(this, emailEditText.getText()
					                                                                   .toString(), passwordEditText
							.getText().toString());
					mProgressDialog.setTitle("Login");
					mProgressDialog.setMessage("Please wait...");
					mProgressDialog.setIndeterminate(true);
					mProgressDialog.show();
					break;
				}
				case R.id.signUpButton: {
					startActivity(new Intent(this, SignUpActivity.class));
					break;
				}
			}
		else
			Utils.INSTANCE.showNoConnectionDialog(this);
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
}