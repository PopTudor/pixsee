package com.marked.pixsee.signup;


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
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.main.MainActivity;
import com.marked.pixsee.signup.di.DaggerSignupComponent;
import com.marked.pixsee.signup.di.SignupModule;

import javax.inject.Inject;

public class SignUpActivity
		extends AppCompatActivity
		implements SignUpNameFragment.SignUpNameFragmentInteraction,
		SignUpEmailFragment.SignUpEmailFragmentInteraction,
		SignUpPassFragment.SignUpPassFragmentInteraction, SignUpContract.View {
	private FragmentManager mFragmentManager;
	private ProgressDialog mProgressDialog;

	@Inject
	SignUpContract.Presenter mPresenter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		mFragmentManager = getSupportFragmentManager();
		mProgressDialog = new ProgressDialog(this);
		DaggerSignupComponent.builder()
				.appComponent(((Pixsee) getApplication()).getAppComponent())
				.activityModule(new ActivityModule(this))
				.signupModule(new SignupModule(this))
				.build().inject(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPresenter.start();
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
		Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void hideDialog() {
		mProgressDialog.hide();
	}

	@Override
	public void setPresenter(SignUpContract.Presenter presenter) {
		mPresenter = presenter;
	}
}

