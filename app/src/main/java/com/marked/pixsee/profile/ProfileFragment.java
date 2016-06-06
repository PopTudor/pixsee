package com.marked.pixsee.profile;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.marked.pixsee.R;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.friendsInvite.FriendsInviteActivity;

public class ProfileFragment extends Fragment {
	private static String USER_EXTRA = "PROFILE_FRAGMENT_USER";
	private User mUser;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment ProfileFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static ProfileFragment newInstance(User user) {
		ProfileFragment fragment = new ProfileFragment();
		Bundle args = new Bundle();
		args.putParcelable(USER_EXTRA, user);
		fragment.setArguments(args);
		return fragment;
	}


	public ProfileFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mUser = getArguments().getParcelable(USER_EXTRA);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
		((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) rootView.findViewById(R.id.toolbar));
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");

		((TextView)rootView.findViewById(R.id.nameTextview)).setText(mUser.getName());
		((TextView)rootView.findViewById(R.id.usernameTextview)).setText(mUser.getUsername());
		ImageButton inviteFriendsImageButton = ((ImageButton)rootView.findViewById(R.id.inviteFriendsImageButton));
		inviteFriendsImageButton.getDrawable()
				.setColorFilter(ContextCompat.getColor(getActivity(),R.color.accent), PorterDuff.Mode.SRC_ATOP);
		inviteFriendsImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), FriendsInviteActivity.class);
				getActivity().startActivity(intent);
			}
		});

		// Inflate the layout for this fragment
		return rootView ;
	}

}
