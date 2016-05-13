package com.marked.pixsee.signup;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.marked.pixsee.R;

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SignUpNameFragmentInteraction] interface
 * to handle interaction events.
 * Use the [SignUpNameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
public class SignUpNameFragment extends Fragment {
	private SignUpNameFragmentInteraction mListener;
	private TextView friendNameTextView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sign_up_name, container, false);
		friendNameTextView = (TextView) rootView.findViewById(R.id.friendNameTextView);

		rootView.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = friendNameTextView.getText().toString().trim();
				if (name.isEmpty())
					Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
				else
					onNextPressed(name);
			}
		});
		return rootView;
	}


	public void onNextPressed(String name) {
		if (mListener != null) mListener.onSaveName(name);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof SignUpNameFragmentInteraction)
			mListener = (SignUpNameFragmentInteraction) context;
		else
			throw new RuntimeException(context.toString() + " must implement SignUpNameFragmentInteraction");

	}


	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	interface SignUpNameFragmentInteraction {
		void onSaveName(String name);
	}


	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment SignUpNameFragment.
	 */
	public static SignUpNameFragment newInstance() {
		return new SignUpNameFragment();
	}
}// Required empty public constructor
