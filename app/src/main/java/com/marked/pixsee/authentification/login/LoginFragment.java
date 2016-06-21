package com.marked.pixsee.authentification.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jakewharton.rxbinding.view.RxView;
import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.R;
import com.marked.pixsee.utility.Utils;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Tudor on 14-Jun-16.
 */
public class LoginFragment extends Fragment {
	private LoginInteractionListener mInteractionListener;
	private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkPlayServices();// check if the user has google play services, else finish
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_login, container, false);
		if (BuildConfig.DEBUG) {
			((EditText) rootView.findViewById(R.id.emailEditText)).setText("tudor14pop@gmail.com");
			((EditText) rootView.findViewById(R.id.passwordEditText)).setText("password");
		}

		Subscription subscription = RxView.clicks(rootView.findViewById(R.id.logInButtonPixy))
				.throttleFirst(10, TimeUnit.SECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<Void>() {
					@Override
					public void call(Void aVoid) {
						if (!Utils.isOnline(getActivity())) Utils.showNoConnectionDialog(getActivity());
						mInteractionListener.onLoginClicked(
								((EditText) getView().findViewById(R.id.emailEditText)).getText().toString(),
								((EditText) getView().findViewById(R.id.passwordEditText)).getText().toString()
						);
					}
				});
		mCompositeSubscription.add(subscription);

		rootView.findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mInteractionListener.onSignupClicked();
			}
		});

		return rootView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mInteractionListener = ((LoginInteractionListener) context);
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement " + LoginInteractionListener.class);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mInteractionListener = null;
		mCompositeSubscription.unsubscribe();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability
						.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.d("***", "This device is not supported.");
				getActivity().finish();
			}
			return false;
		}
		return true;
	}

	private static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public static Fragment newInstance() {
		Fragment fragment = new LoginFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	public interface LoginInteractionListener {
		void onSignupClicked();

		void onLoginClicked(String email, String password);
	}
}
