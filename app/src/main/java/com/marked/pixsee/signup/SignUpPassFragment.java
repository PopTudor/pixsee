package com.marked.pixsee.signup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.marked.pixsee.R;


public class SignUpPassFragment extends Fragment {
	private Button nextButton;
	private AutoCompleteTextView passwordEditText;

	public SignUpPassFragment() {
	}

	private SignUpPassFragmentInteraction mListener;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sign_up_pass, container, false);
		nextButton = (Button) rootView.findViewById(R.id.nextButton);
		passwordEditText = (AutoCompleteTextView) rootView.findViewById(R.id.passwordEditText);
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = passwordEditText.getText().toString().trim();
				if (password.isEmpty())
					Toast.makeText(getContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
				else if (password.length() < 6)
					Toast.makeText(getContext(), "The password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
				else
					onNextPressed(password);
			}
		});

		// Inflate the layout for this fragment
		return rootView;
	}

	// TODO: Rename method, update argument and hook method into UI event
	void onNextPressed(String password) {
		if (mListener != null) {
			mListener.onSavePassword(password);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof SignUpPassFragmentInteraction) {
			mListener = (SignUpPassFragmentInteraction) context;
		} else throw new RuntimeException(context.toString() + " must implement SignUpEmailFragmentInteraction");
	}


	interface SignUpPassFragmentInteraction {
		// TODO: Update argument type and name
		void onSavePassword(String password);
	}


	public static SignUpPassFragment newInstance() {
		return new SignUpPassFragment();
	}
}// Required empty public constructor
