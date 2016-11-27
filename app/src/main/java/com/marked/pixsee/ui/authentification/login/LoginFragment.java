package com.marked.pixsee.ui.authentification.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding.view.RxView;
import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.R;
import com.marked.pixsee.utility.Utils;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Tudor on 14-Jun-16.
 */
public class LoginFragment extends Fragment {
	private static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private LoginInteractionListener mInteractionListener;
	private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

	public static Fragment newInstance() {
		Fragment fragment = new LoginFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final View rootView = inflater.inflate(R.layout.fragment_login, container, false);
		if (BuildConfig.DEBUG) {
			((EditText) rootView.findViewById(R.id.emailEditText)).setText("tudor14pop@gmail.com");
			((EditText) rootView.findViewById(R.id.passwordEditText)).setText("password");
		}

		RxView.clicks(rootView.findViewById(R.id.logInButtonPixy))
				.throttleFirst(10, TimeUnit.SECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<Void>() {
					@Override
					public void call(Void aVoid) {
						if (!Utils.isOnline(getActivity()))
							Utils.showNoConnectionDialog(getActivity());
						mInteractionListener.onLoginClicked(
								((EditText) rootView.findViewById(R.id.emailEditText)).getText().toString(),
								((EditText) rootView.findViewById(R.id.passwordEditText)).getText().toString()
						);
					}
				});

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
	}

	public interface LoginInteractionListener {
		void onSignupClicked();

		void onLoginClicked(String email, String password);
	}
}
