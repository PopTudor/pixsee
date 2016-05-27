package com.marked.pixsee.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.R;
import com.marked.pixsee.friends.data.User;
import com.marked.pixsee.databinding.ActivityLogInBinding;
import com.marked.pixsee.injection.components.DaggerActivityComponent;
import com.marked.pixsee.injection.modules.ActivityModule;
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

	private ActivityLogInBinding bind;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bind = DataBindingUtil.setContentView(this, R.layout.activity_log_in);

		DaggerActivityComponent.builder().activityModule(new ActivityModule(this)).build()
		                       .inject(this);
		if (BuildConfig.DEBUG)
			bind.setUser(new User("", "", "tudor08pop@yahoo.com", "", "password"));
	}

	public void onResume() {
		super.onResume();
		mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver,
		                                             new IntentFilter(GCMConstants.ACTION_LOGIN));
		mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver,
		                                             new IntentFilter(GCMConstants.ACTION_ERROR));
	}

	public void onPause() {
		mBroadcastManagerastManager.unregisterReceiver(mRegistrationBroadcastReceiver);
		super.onPause();
	}

	public void onClick(View v) {
		// if we got internet, process the click. else tell them to activate it
		if (Utils.isOnline(this))
			switch (v.getId()) {
				case R.id.logInButtonPixy: {
					if (!new DataValidation(this)
							.validate(bind.emailEditText.getText().toString(),
							          bind.passwordEditText.getText().toString()))
						return;
					LogInRegistrationIntentService
							.startActionLogin(this, bind.emailEditText.getText().toString(),
							                  bind.passwordEditText.getText().toString());
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
}