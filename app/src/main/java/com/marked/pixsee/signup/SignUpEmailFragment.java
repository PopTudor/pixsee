package com.marked.pixsee.signup;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.marked.pixsee.R;
import com.marked.pixsee.utility.Utils;

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SignUpEmailFragmentInteraction] interface
 * to handle interaction events.
 * Use the [SignUpEmailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
public class SignUpEmailFragment extends Fragment {
	private SignUpEmailFragmentInteraction mListener;
	private TextView emailEditText;
	private TextView hiUser;
	private Button nextButton;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sign_up_email, container, false);
		hiUser = (TextView) rootView.findViewById(R.id.hiUser);
		emailEditText = (TextView) rootView.findViewById(R.id.emailEditText);

		hiUser.setText("Beautiful name " + getArguments().getString(NAME_TAG));
		nextButton = (Button) rootView.findViewById(R.id.nextButton);
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!Utils.isOnline(getActivity())) {
					Utils.showNoConnectionDialog(getActivity());
				} else {
					String email = emailEditText.getText().toString().trim();
					if (email.isEmpty())
						Toast.makeText(getActivity(), "Please enter an email", Toast.LENGTH_SHORT).show();
					else if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
						onNextPressed(email);
					else
						Toast.makeText(getActivity(), "Invalid email adress", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Inflate the layout for this fragment
		return rootView;
	}


	// TODO: Rename method, update argument and hook method into UI event
	public void onNextPressed(String emailEditText) {
		if (mListener != null) {
			mListener.onSaveEmail(emailEditText);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof SignUpEmailFragmentInteraction)
			mListener = (SignUpEmailFragmentInteraction) context;
		else
			throw new RuntimeException(context.toString() + " must implement SignUpEmailFragmentInteraction");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	interface SignUpEmailFragmentInteraction {
		// TODO: Update argument type and name
		void onSaveEmail(String emailEditText);
	}

	public static final String NAME_TAG = "NAME";

	public static SignUpEmailFragment newInstance(String name) {
		Bundle bundle = new Bundle();
		bundle.putString(NAME_TAG, name);
		SignUpEmailFragment fragment = new SignUpEmailFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
}// Required empty public constructor