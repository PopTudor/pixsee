package com.marked.pixsee.authentification;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.marked.pixsee.Pixsee;
import com.marked.pixsee.R;
import com.marked.pixsee.authentification.di.AuthenticationModule;
import com.marked.pixsee.authentification.di.DaggerAuthenticationComponent;
import com.marked.pixsee.authentification.login.LoginFragment;
import com.marked.pixsee.authentification.signup.SignUpEmailFragment;
import com.marked.pixsee.authentification.signup.SignUpNameFragment;
import com.marked.pixsee.authentification.signup.SignUpPassFragment;
import com.marked.pixsee.main.MainActivity;
import com.pixsee.di.modules.ActivityModule;

import javax.inject.Inject;

public class AuthenticationActivity
		extends AppCompatActivity
		implements SignUpNameFragment.SignUpNameFragmentInteraction,
		SignUpEmailFragment.SignUpEmailFragmentInteraction,
		SignUpPassFragment.SignUpPassFragmentInteraction,
		AuthenticationContract.View,LoginFragment.LoginInteractionListener {
	private FragmentManager mFragmentManager;
	private ProgressDialog mProgressDialog;
	@Inject
	AuthenticationContract.Presenter mPresenter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		mFragmentManager = getSupportFragmentManager();
		mProgressDialog = new ProgressDialog(this);
		DaggerAuthenticationComponent.builder()
				.appComponent(((Pixsee) getApplication()).getAppComponent())
				.activityModule(new ActivityModule(this))
				.authenticationModule(new AuthenticationModule(this))
				.build().inject(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPresenter.attach();
	}

	@Override
	protected void onDestroy() {
		mProgressDialog.dismiss();
		super.onDestroy();
	}

	@Override
	public void onSaveName(@NonNull String name) {
		mPresenter.onSaveName(name);
	}

	@Override
	public void onSaveEmail(@NonNull String email) {
		mPresenter.onSaveEmail(email);
	}

	@Override
	public void showDialog(String title, String message) {
		mProgressDialog.setTitle(title);
		mProgressDialog.setMessage(message);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.show();
	}

	@Override
	public void signupComplete(String name, String email, String username, String password) {
	}

	@Override
	public void showMainScreen() {
		Intent toStart = new Intent(this, MainActivity.class);
		toStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		toStart.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(toStart);
	}

	@Override
	public void showLoginStep() {
		mFragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, LoginFragment.newInstance())
				.commit();
	}

	@Override
	public void onSavePassword(@NonNull String password) {
		mPresenter.onSavePassword(password);
	}

	@Override
	public void onSaveUsername(String username) {
		mPresenter.onSaveUsername(username);
	}

	@Override
	public void showSignupStepPassword() {
		mFragmentManager.beginTransaction()
				.add(R.id.fragmentContainer, SignUpPassFragment.newInstance())
				.addToBackStack("signupPass")
				.commit();
	}

	@Override
	public void showSignupStepName() {
		mFragmentManager.beginTransaction()
				.add(R.id.fragmentContainer, SignUpNameFragment.newInstance())
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void showSignupStepEmail(String name) {
		mFragmentManager.beginTransaction()
				.add(R.id.fragmentContainer, SignUpEmailFragment.newInstance(name))
				.addToBackStack("signupEmail")
				.commit();
	}

	@Override
	public void showToast(String message) {
		Toast.makeText(AuthenticationActivity.this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void hideDialog() {
		mProgressDialog.hide();
	}

	@Override
	public void setPresenter(AuthenticationContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void onSignupClicked() {
		showSignupStepName();
	}

	@Override
	public void onLoginClicked(String email, String password) {
		mPresenter.handleLogin(email, password);
	}
}

