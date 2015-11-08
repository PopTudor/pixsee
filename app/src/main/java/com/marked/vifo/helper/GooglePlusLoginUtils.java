package com.marked.vifo.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;

public class GooglePlusLoginUtils implements ConnectionCallbacks, OnConnectionFailedListener, OnClickListener,
        ResultCallback<People.LoadPeopleResult> {
	public static final String NAME     = "name";
	public static final String EMAIL    = "email";
	public static final String ICONURL  = "photo";
	public static final String COVERURL = "coverURL";

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN       = 0;
	private static final int PROFILE_PIC_SIZE = 400;
	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;
	private boolean          mIntentInProgress;
	private boolean          mSignInClicked;
	private ConnectionResult mConnectionResult;

	private Button           mSignInButton;
	private Context          mContext;
	private GPlusLoginStatus loginstatus;

	public GooglePlusLoginUtils(Context mContext, View view) {
//		Log.i(TAG, "GooglePlusLoginUtils");
		this.mContext = mContext;
		this.mSignInButton = (Button) view;
		// Initializing google plus api client

		mGoogleApiClient = new GoogleApiClient.Builder(mContext).addConnectionCallbacks(this)
																.addOnConnectionFailedListener(this).addApi(Plus.API)
																.addScope(Plus.SCOPE_PLUS_LOGIN)
																.addScope(Plus.SCOPE_PLUS_PROFILE)
																.addScope(new Scope(Scopes.PLUS_LOGIN))
																.addScope(new Scope(Scopes.PLUS_ME))
																.addScope(new Scope(Scopes.CLOUD_SAVE)).build();
	}

	public void setLoginStatus(GPlusLoginStatus loginStatus) {
		this.loginstatus = loginStatus;
		if (mSignInButton != null)
			mSignInButton.setOnClickListener(this);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
//		Log.i(TAG, "onConnectionFailed");
//		Log.i(TAG, "Error Code " + result);
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), (Activity) mContext, 0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	public void setSignInClicked(boolean value) {
		mSignInClicked = value;
	}

	public void setIntentInProgress(boolean value) {
		mIntentInProgress = value;
	}

	public void connect() {
		mGoogleApiClient.connect();
	}

	public void reconnect() {
		if (!mGoogleApiClient.isConnecting()) {
			mGoogleApiClient.connect();
		}
	}

	public void disconnect() {
		if (mGoogleApiClient.isConnected()) {
//			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
			mGoogleApiClient.disconnect();
		}
	}

	private void signInWithGplus() {
//		Log.i(TAG, "signInWithGplus");
		if (mGoogleApiClient != null && !mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	private void resolveSignInError() {
//		Log.i(TAG, "resolveSignInError");
		if (mConnectionResult != null && mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult((Activity) mContext, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
//		Log.d(TAG, "onConnected");
		mSignInClicked = false;
//		Toast.makeText(mContext, "User is connected!", Toast.LENGTH_LONG).show();
		Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
		getProfileInformation();
	}

	private void getProfileInformation() {
//		Log.d(TAG, "getProfileInformation");
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

				String personName = null;
				String personImageURL = null;
				String personCoverURL = null;
				String personEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

				if (currentPerson.hasName())
					personName = currentPerson.getDisplayName();
				if (currentPerson.hasImage())
					personImageURL = currentPerson.getImage().getUrl();

				if (currentPerson.hasCover())
					personCoverURL = currentPerson.getCover().getCoverPhoto().getUrl();

				// by default the profile url gives 50x50 px image only
				// we can replace the value with whatever dimension we want by
				// replacing sz=X
				personImageURL = personImageURL.substring(0, personImageURL.length() - 2) + PROFILE_PIC_SIZE;

				Bundle profile = new Bundle();
				profile.putString(NAME, personName);
				profile.putString(EMAIL, personEmail);
				profile.putString(ICONURL, personImageURL);
				profile.putString(COVERURL, personCoverURL);
				loginstatus.OnSuccessGPlusLogin(profile);

			} else {
//				Toast.makeText(mContext, "Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
//		Log.d(TAG, "onConnectionSuspended");
		mGoogleApiClient.connect();
	}

	@Override
	public void onClick(View v) {
		signInWithGplus();
	}

	public void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != ((Activity) mContext).RESULT_OK) {
				setSignInClicked(false);
			}
			setIntentInProgress(false);
			reconnect();
		}
	}

	@Override
	public void onResult(People.LoadPeopleResult loadPeopleResult) {

	}


	public interface GPlusLoginStatus {
		void OnSuccessGPlusLogin(Bundle profile) throws IOException;
	}


}