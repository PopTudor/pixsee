package com.marked.pixsee.ui.profile;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.marked.pixsee.Pixsee;
import com.marked.pixsee.R;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.components.DaggerActivityComponent;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.ui.entry.EntryActivity;
import com.marked.pixsee.ui.friendsInvite.FriendsInviteActivity;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;

public class ProfileFragment extends Fragment implements ProfileContract.View {
	private static String USER_EXTRA = "PROFILE_FRAGMENT_USER";
	private User mUser;
	private PictureAdapter mPictureAdapter;
	private ProfileFragmentInteraction mProfileFragmentInteraction;
	@Inject
	ProfileContract.Presenter mPresenter;


	public ProfileFragment() {
		// Required empty public constructor
	}

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

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mProfileFragmentInteraction = (ProfileFragmentInteraction) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.getPackageName() + " must implement " + ProfileFragmentInteraction.class.getCanonicalName());
		}
		mUser = getArguments().getParcelable(USER_EXTRA);

		ActivityComponent component = DaggerActivityComponent
				                              .builder()
				                              .sessionComponent(((Pixsee) getActivity().getApplication()).getSessionComponent())
				                              .activityModule(new ActivityModule((AppCompatActivity) getActivity()))
				                              .build();
		DaggerProfileComponent.builder().activityComponent(component)
				.profileModule(new ProfileModule(this))
				.build()
				.inject(this);

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mPictureAdapter = new PictureAdapter();
		// Must be done during an initialization phase like onCreate
		RxPermissions.getInstance(getActivity())
				.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
				.subscribe(new Action1<Boolean>() {
					@Override
					public void call(Boolean granted) {
						if (granted) { // Always true pre-M
							// I can control the camera now
						} else {
							// Oups permission denied
//							getActivity().onBackPressed();
						}
					}
				});
		mPresenter.attach();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
		((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) rootView.findViewById(R.id.toolbar));
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");

		((TextView) rootView.findViewById(R.id.nameTextview)).setText(mUser.getName());
		((TextView) rootView.findViewById(R.id.usernameTextview)).setText(mUser.getUserName());
//		if (mUser.getIconUrl() != null) // // TODO: 12-Jun-16 remove this and make mUser.getIconUrl() to always have an Url
//			((SimpleDraweeView) rootView.findViewById(R.id.iconSimpleDraweeView)).setImageURI(Uri.parse(mUser.getIconUrl()));

		ImageButton inviteFriendsImageButton = ((ImageButton) rootView.findViewById(R.id.inviteFriendsImageButton));
		inviteFriendsImageButton.getDrawable()
				.setColorFilter(ContextCompat.getColor(getActivity(), R.color.accent), PorterDuff.Mode.SRC_ATOP);
		inviteFriendsImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.inviteFriendsClicked();
			}
		});

		rootView.findViewById(R.id.iconSimpleDraweeView).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mProfileFragmentInteraction.onTakeProfilePictureClick();
			}
		});
		((Toolbar) rootView.findViewById(R.id.toolbar)).getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
		// Inflate the layout for this fragment
		RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.cardRecyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
		recyclerView.setAdapter(mPictureAdapter);

		return rootView;
	}

	@Override
	public void showFriendsInvite() {
		Intent intent = new Intent(getActivity(), FriendsInviteActivity.class);
		getActivity().startActivity(intent);
	}

	@Override
	public void setData(List<String> list) {
		PictureAdapter.setDataset(list);
		mPictureAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_more, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.logoutButton:
				mPresenter.logOut();
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor editor = preferences.edit();
				editor.clear();
				editor.commit();
				getActivity().deleteDatabase(DatabaseContract.DATABASE_NAME);
				Intent intent = new Intent(getActivity(), EntryActivity.class);
				getActivity().startActivity(intent);
				getActivity().finish();
				return true;
			case R.id.aboutButton:
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("About")
						.setCancelable(true)
						.setIcon(R.mipmap.ic_launcher)
						.setMessage("Version 1.0.0.0\nAuthor Pop Tudor\nContact tudor.pop92@e-uvt.ro")
						.show();
				return true;
			default:
				Toast.makeText(getActivity(), "Menu not working!", Toast.LENGTH_SHORT).show();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setProfilePicture(File profilePicture) {
		((SimpleDraweeView) getView().findViewById(R.id.iconSimpleDraweeView)).setImageURI(Uri.parse("file://" + profilePicture.getAbsolutePath()));
//		mUser.setIconUrl("file://" + profilePicture.getAbsolutePath());
		mPresenter.saveAppUser(mUser);
	}

	@Override
	public void setPresenter(ProfileContract.Presenter presenter) {
		mPresenter = presenter;
	}

	public interface ProfileFragmentInteraction {
		void onTakeProfilePictureClick();
	}

}
