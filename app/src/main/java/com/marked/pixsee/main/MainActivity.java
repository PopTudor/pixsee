package com.marked.pixsee.main;

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
import com.marked.pixsee.R;
import com.marked.pixsee.chat.ChatActivity;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.friends.FriendFragment;
import com.marked.pixsee.injection.Injectable;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.main.strategy.PictureActionStrategy;
import com.marked.pixsee.main.strategy.ProfilePictureStrategy;
import com.marked.pixsee.profile.ProfileFragment;
import com.marked.pixsee.selfie.PictureDetailSendFragment;
import com.marked.pixsee.selfie.PictureDetailShareFragment;
import com.marked.pixsee.selfie.SelfieFragment;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;

import javax.inject.Inject;

import rx.Observable;

import static com.marked.pixsee.selfie.SelfieFragment.OnSelfieInteractionListener;

public class MainActivity
		extends AppCompatActivity
		implements MainContract.View, Injectable, FriendFragment.FriendFragmentInteractionListener,
		PictureDetailShareFragment.OnPictureDetailShareListener, OnSelfieInteractionListener,
		PictureDetailSendFragment.OnPictureDetailSendListener, ProfileFragment.ProfileFragmentInteraction {
	public static final int START_CAMERA_REQUEST_CODE = 100;
	@Inject
	MainContract.Presenter mPresenter;
	private AHBottomNavigation mBottomNavigation;
	private PictureActionStrategy mPictureActionStrategy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		injectComponent();
		mBottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
		// Create items
		AHBottomNavigationItem item1 = new AHBottomNavigationItem(
				"Chats", ContextCompat.getDrawable(this, R.drawable.ic_chat_24dp), ContextCompat.getColor(this, R.color.white));
		AHBottomNavigationItem item2 = new AHBottomNavigationItem(
				"Camera", ContextCompat.getDrawable(this, R.drawable.ic_photo_camera_24dp), ContextCompat.getColor(this, R.color.white));
		AHBottomNavigationItem item3 = new AHBottomNavigationItem(
				"Profile", ContextCompat.getDrawable(this, R.drawable.ic_person_24dp), ContextCompat.getColor(this, R.color.white));

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
		DaggerMainComponent.builder()
				.activityModule(new ActivityModule(this))
				.mainModule(new MainModule())
				.build()
				.inject(this);
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
				.replace(R.id.mainContainer, ProfileFragment.newInstance(user), "profile")
				.commit();
	}

	@Override
	public void showChat(boolean show) {
		Fragment fragment = FriendFragment.newInstance();
		getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, fragment).commit();
	}

	@Override
	public void showCamera(@NotNull PictureActionStrategy actionStrategy) {
		mPictureActionStrategy = actionStrategy;
		Fragment fragment = SelfieFragment.newInstance();
		getSupportFragmentManager().beginTransaction().add(R.id.mainContainer, fragment, "camera").addToBackStack(null).commit();
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
		((SelfieFragment) getSupportFragmentManager().findFragmentByTag("camera")).resumeSelfie();
	}

	@Override
	public void pictureTaken(File picture) {
		if (mPictureActionStrategy instanceof ProfilePictureStrategy) {
			getSupportFragmentManager().popBackStackImmediate(); // pop the camera fragment
			((ProfileFragment) getSupportFragmentManager().findFragmentByTag("profile")).setProfilePicture(picture);
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
		((SelfieFragment) getSupportFragmentManager().findFragmentByTag("camera")).resumeSelfie();
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

		public OnTabSelectedHandler(MainContract.Presenter presenterWeakReference) {
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
