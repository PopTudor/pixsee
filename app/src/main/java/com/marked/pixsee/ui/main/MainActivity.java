package com.marked.pixsee.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.marked.pixsee.Pixsee;
import com.marked.pixsee.R;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.injection.Injectable;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.ui.chat.ChatActivity;
import com.marked.pixsee.ui.friends.FriendFragment;
import com.marked.pixsee.ui.main.strategy.PictureActionStrategy;
import com.marked.pixsee.ui.main.strategy.ProfilePictureStrategy;
import com.marked.pixsee.ui.profile.ProfileFragment;
import com.marked.pixsee.ui.selfie.PictureDetailSendFragment;
import com.marked.pixsee.ui.selfie.PictureDetailShareFragment;
import com.marked.pixsee.ui.selfie.SelfieFragment;

import java.io.File;
import java.lang.ref.WeakReference;

import javax.inject.Inject;

import rx.Observable;

import static com.marked.pixsee.ui.selfie.SelfieFragment.OnSelfieInteractionListener;

public class MainActivity
		extends AppCompatActivity
		implements MainContract.View, Injectable, FriendFragment.FriendFragmentInteractionListener,
		PictureDetailShareFragment.OnPictureDetailShareListener, OnSelfieInteractionListener,
		PictureDetailSendFragment.OnPictureDetailSendListener, ProfileFragment.ProfileFragmentInteraction {
	public static final String FRAGMENT_CHATS = "Chats";
	public static final String FRAGMENT_PROFILE = "Profile";
	public static final String FRAGMENT_CAMERA = "Camera";
	public static final int START_CAMERA_REQUEST_CODE = 100;
	private AHBottomNavigation mBottomNavigation;
	private PictureActionStrategy mPictureActionStrategy;
	private MainComponent mMainComponent;
	@Inject
	MainContract.Presenter mPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		injectComponent();
		mBottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
		// Create items
		int whiteColor = ContextCompat.getColor(this, R.color.white);
		AHBottomNavigationItem item1 = new AHBottomNavigationItem(FRAGMENT_CHATS,
				                                                         ContextCompat.getDrawable(this, R.drawable.ic_chat_24dp),
				                                                         whiteColor);
		AHBottomNavigationItem item2 = new AHBottomNavigationItem(FRAGMENT_CAMERA,
				                                                         ContextCompat.getDrawable(this, R.drawable.ic_photo_camera_24dp),
				                                                         whiteColor);
		AHBottomNavigationItem item3 = new AHBottomNavigationItem(FRAGMENT_PROFILE,
				                                                         ContextCompat.getDrawable(this, R.drawable.ic_person_24dp),
				                                                         whiteColor);

		// Add items
		mBottomNavigation.addItem(item1);
		mBottomNavigation.addItem(item2);
		mBottomNavigation.addItem(item3);

		mBottomNavigation.setDefaultBackgroundColor(Color.WHITE);
		mBottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.primary));
		mBottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.dark_inactive_icons));

		mBottomNavigation.setOnTabSelectedListener(new OnTabSelectedHandler(mPresenter));
		mPresenter.attach();
	}

	@Override
	public void injectComponent() {
		ActivityModule activityModule = new ActivityModule(this);
		mMainComponent = DaggerMainComponent.builder()
				.activityModule(activityModule)
				.mainModule(new MainModule())
				                 .sessionComponent(Pixsee.getSessionComponent())
				                 .build();
		mMainComponent.inject(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		newFriendRequest(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		newFriendRequest(intent);
	}

	@Override
	public void refreshFriendList() {
		FriendFragment chatFragment = (FriendFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_CHATS);
		if (chatFragment != null) {
			chatFragment.refreshFriendList();
		}
	}

	void newFriendRequest(Intent intent){
		/* this will enter when the user is not using the app and gets a friend request from FCM */
		User user = intent.getParcelableExtra(getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION));
		if (user!=null)
			mPresenter.friendRequest(user);
	}

	@Override
	public void setPresenter(MainContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void showProfile(@NonNull User user) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.mainContainer, ProfileFragment.newInstance(user), FRAGMENT_PROFILE)
				.commit();
	}

	@Override
	public void showChat(boolean show) {
		Fragment fragment = FriendFragment.newInstance();
		getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, fragment, FRAGMENT_CHATS).commit();
	}

	@Override
	public void showCamera(@NonNull PictureActionStrategy actionStrategy) {
		mPictureActionStrategy = actionStrategy;
		Fragment fragment = SelfieFragment.newInstance();
		getSupportFragmentManager().beginTransaction().add(R.id.mainContainer, fragment, FRAGMENT_CAMERA).addToBackStack(null).commit();
	}

	@Override
	public void hideBottomNavigation() {
		findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
	}

	@Override
	public void selfieFragmentDesroyed() {
		mBottomNavigation.setVisibility(View.VISIBLE);
	}

	@Override
	public Observable<Bitmap> getPicture() {
		return ((SelfieFragment) getSupportFragmentManager().findFragmentById(R.id.mainContainer)).getPicture();
	}

	@Override
	public void stop() {
		((SelfieFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_CAMERA)).resumeSelfie();
	}

	@Override
	public void pictureTaken(File picture) {
		if (mPictureActionStrategy instanceof ProfilePictureStrategy) {
			getSupportFragmentManager().popBackStackImmediate(); // pop the camera fragment
			((ProfileFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_PROFILE)).setProfilePicture(picture);
		}
	}

	@Override
	public void showTakenPictureActions() {
		mPictureActionStrategy.showAction(this);
	}

	/**
	 * This get
	 */
	@Override
	public void resumeSelfie() {
		((SelfieFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_CAMERA)).resumeSelfie();
	}

	@Override
	public AlertDialog showFriendRequestDialog(@NonNull final User user) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setCancelable(false)
				.setTitle("Friend Request")
				.setMessage(String.format("Accept friend request from %s?", user.getName()));

		builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mPresenter.friendRequest(user, true);
			}
		});
		builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mPresenter.friendRequest(user, false);
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}

	/***************************
	 * OnFriendFragmentInteractionListener
	 *************************/
	@Override
	public void onFriendClick(User friend) {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(ChatActivity.EXTRA_CONTACT, friend);
		startActivity(intent);
	}

	/***************************
	 * OnProfilePictureInteraction
	 *************************/
	@Override
	public void onTakeProfilePictureClick() {
		mPresenter.profileImageClicked();
	}

	private static class OnTabSelectedHandler implements AHBottomNavigation.OnTabSelectedListener {
		private WeakReference<MainContract.Presenter> mPresenterWeakReference;

		OnTabSelectedHandler(MainContract.Presenter presenterWeakReference) {
			mPresenterWeakReference = new WeakReference<>(presenterWeakReference);
		}

		@Override
		public void onTabSelected(int position, boolean wasSelected) {
			switch (position) {
				case 0:
					if (!wasSelected)
						mPresenterWeakReference.get().chatTabClicked();
					break;
				case 1:
					if (!wasSelected)
						mPresenterWeakReference.get().cameraTabClicked();
					break;
				case 2:
					if (!wasSelected)
						mPresenterWeakReference.get().profileTabClicked();
					break;
			}

		}
	}
}
