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

import com.marked.pixsee.R;
import com.marked.pixsee.login.LoginAPI;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.service.LogInRegistrationIntentService;
import com.marked.pixsee.service.RegistrationBroadcastReceiver;
import com.marked.pixsee.utility.GCMConstants;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity
		extends AppCompatActivity
		implements SignUpNameFragment.SignUpNameFragmentInteraction,
				           SignUpEmailFragment.SignUpEmailFragmentInteraction,
				           SignUpPassFragment.SignUpPassFragmentInteraction {
	private FragmentManager mFragmentManager;
	private ProgressDialog mProgressDialog;
	private RegistrationBroadcastReceiver mRegistrationBroadcastReceiver;
	private LocalBroadcastManager mBroadcastManagerastManager;

	private String mName;
	private String mEmail;
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
	}

	@Override
	public void onResume() {
		super.onResume();
		mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMConstants.ACTION_SIGNUP));
		mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMConstants.ACTION_ERROR));
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
		checkEmail(mEmail);
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

	/**
	 * Sends a request to the server to check if the email is already in the database.
	 * If that is true the user already has an account and we should tell him that
	 * Else proceed to the next step
	 *
	 * @param email the email adress to verify on the server
	 */
	private void checkEmail(String email) {
		Retrofit retrofit = new Retrofit.Builder()
				                    .addConverterFactory(GsonConverterFactory.create())
				                    .baseUrl(ServerConstants.SERVER)
				                    .build();
		LoginAPI service = retrofit.create(LoginAPI.class);
		service.hasAccount(email)
				.enqueue(new Callback<Void>() {
					         @Override
					         public void onResponse(Call<Void> call, Response<Void> response) {
						         mProgressDialog.dismiss();
						         if (response != null && response.isSuccess()) {/* if hasAccount returns 200 -> user registered*/
							         Toast.makeText(SignUpActivity.this, "You already have an account", Toast.LENGTH_SHORT).show();
						         } else /* go to next step*/
							         mFragmentManager.beginTransaction().add(R.id.fragmentContainer, SignUpPassFragment.newInstance())
									         .addToBackStack("signupPass").commit();
					         }

					         @Override
					         public void onFailure(Call<Void> call, Throwable t) {
						         mProgressDialog.dismiss();
						         if (t instanceof SocketTimeoutException)
							         Toast.makeText(SignUpActivity.this, "Timeout Error", Toast.LENGTH_SHORT).show();
						         else
							         t.printStackTrace();
					         }
				         }
				);
	}
}

