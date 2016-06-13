package com.marked.pixsee.signup;


import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.marked.pixsee.Pixsee;
import com.marked.pixsee.R;
import com.marked.pixsee.service.LogInRegistrationIntentService;
import com.marked.pixsee.service.RegistrationBroadcastReceiver;
import com.marked.pixsee.signup.di.DaggerSignupComponent;
import com.marked.pixsee.signup.di.SignupModule;
import com.marked.pixsee.utility.GCMConstants;

import javax.inject.Inject;

public class SignUpActivity
		extends AppCompatActivity
		implements SignUpNameFragment.SignUpNameFragmentInteraction,
				           SignUpEmailFragment.SignUpEmailFragmentInteraction,
				           SignUpPassFragment.SignUpPassFragmentInteraction,SignupContract.View {
	private FragmentManager mFragmentManager;
	private ProgressDialog mProgressDialog;
	private RegistrationBroadcastReceiver mRegistrationBroadcastReceiver;
	private LocalBroadcastManager mBroadcastManagerastManager;

	@Inject
	SignupContract.Presenter mPresenter;
	private String mName;
	private String mEmail;
	private String mUsername;
	private String mPassword;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		mFragmentManager = getSupportFragmentManager();
		mProgressDialog = new ProgressDialog(this);
		mRegistrationBroadcastReceiver = new RegistrationBroadcastReceiver(new DialogRegistration(mProgressDialog, this));
		mBroadcastManagerastManager = LocalBroadcastManager.getInstance(this);
		mFragmentManager.beginTransaction().add(R.id.fragmentContainer, SignUpNameFragment.newInstance()).commit();
		DaggerSignupComponent.builder()
				.appComponent(((Pixsee) getApplication()).getAppComponent())
				.signupModule(new SignupModule(this))
				.build().inject(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMConstants.ACTION_SIGNUP));
		mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMConstants.ACTION_ERROR));
		mPresenter.start();
	}

	@Override
	public void onPause() {
		mBroadcastManagerastManager.unregisterReceiver(mRegistrationBroadcastReceiver);
		super.onPause();
	}

	@Override
	public void onSaveName(@NonNull String name) {
		mName = name;
		mFragmentManager.beginTransaction().add(R.id.fragmentContainer, SignUpEmailFragment.newInstance(name)).addToBackStack("signupEmail").commit();
	}

	@Override
	public void onSaveEmail(@NonNull String email) {
		mProgressDialog.show();
		mEmail = email;
		mPresenter.checkEmail(mEmail);
	}

	@Override
	public void onSavePassword(@NonNull String password) {
		mPassword = password;
		LogInRegistrationIntentService.startActionSignup(this, mName, mEmail, mPassword);
		mProgressDialog.setTitle("Signup");
		mProgressDialog.setMessage("Please wait...");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.show();
	}

	@Override
	public void showSignUpPasswordStep() {
		mFragmentManager.beginTransaction()
				.add(R.id.fragmentContainer, SignUpPassFragment.newInstance())
				.addToBackStack("signupPass")
				.commit();
	}

	@Override
	public void showToast(String message) {
		Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void hideDialog() {
		mProgressDialog.hide();
	}

	@Override
	public void setPresenter(SignupContract.Presenter presenter) {
		mPresenter = presenter;
	}
}

