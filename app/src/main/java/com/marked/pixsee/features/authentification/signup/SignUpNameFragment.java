package com.marked.pixsee.features.authentification.signup;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.marked.pixsee.BuildConfig;
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
	private AutoCompleteTextView userTextView;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment SignUpNameFragment.
	 */
	public static SignUpNameFragment newInstance() {
		return new SignUpNameFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sign_up_name, container, false);
		userTextView = (AutoCompleteTextView) rootView.findViewById(R.id.nameEditText);

		rootView.findViewById(R.id.finishButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onNextPressed(userTextView.getText().toString().trim());
			}
		});
		userTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) onNextPressed(v.getText().toString().trim());
				return false;
			}
		});
		if (BuildConfig.DEBUG) {
			userTextView.setText("Tudor");
		}
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	}

	public void onNextPressed(String name) {
		if (name==null || name.isEmpty()) {
			Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
			return;
		}
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


	public interface SignUpNameFragmentInteraction {
		void onSaveName(String name);
	}
}// Required empty public constructor
