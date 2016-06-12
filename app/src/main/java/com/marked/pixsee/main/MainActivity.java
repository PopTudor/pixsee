package com.marked.pixsee.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.R;
import com.marked.pixsee.chat.ChatActivity;
import com.marked.pixsee.chat.GCMListenerService;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.entry.EntryActivity;
import com.marked.pixsee.selfie.DetailFragment;
import com.marked.pixsee.selfie.DetailFragment.OnDetailInteractionListener;
import com.marked.pixsee.selfie.SelfieFragment;
import com.marked.pixsee.friends.FriendFragment;
import com.marked.pixsee.friends.data.FriendConstants;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.main.di.DaggerMainComponent;
import com.marked.pixsee.main.di.MainModule;
import com.marked.pixsee.profile.ProfileFragment;
import com.marked.pixsee.profile.ProfileFragment.ProfileInteraction;
import com.marked.pixsee.profile.ProfilePictureDetail;

import java.io.File;
import java.lang.ref.WeakReference;

import javax.inject.Inject;

import rx.Observable;

import static android.support.v4.app.NotificationCompat.FLAG_AUTO_CANCEL;
import static com.marked.pixsee.selfie.SelfieFragment.OnSelfieInteractionListener;
import static com.marked.pixsee.selfie.SelfieFragment.newInstance;

public class MainActivity
		extends AppCompatActivity
		implements MainContract.View, FriendFragment.FriendFragmentInteractionListener, GCMListenerService.Callback,
		ProfileInteraction, OnDetailInteractionListener, OnSelfieInteractionListener, ProfilePictureDetail.OnProfilePictureDetailListener {
	public static final int START_CAMERA_REQUEST_CODE = 100;
	@Inject
	MainContract.Presenter mPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GCMListenerService.addCallback(this);

		DaggerMainComponent.builder()
				.activityModule(new ActivityModule(this))
				.mainModule(new MainModule(this))
				.build()
				.inject(this);
		AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
		// Create items
		AHBottomNavigationItem item1 = new AHBottomNavigationItem(
				"Chats", ContextCompat.getDrawable(this, R.drawable.ic_chat_24dp), ContextCompat.getColor(this, R.color.white));
		AHBottomNavigationItem item2 = new AHBottomNavigationItem(
				"Camera", ContextCompat.getDrawable(this, R.drawable.ic_photo_camera_24dp), ContextCompat.getColor(this, R.color.white));
		AHBottomNavigationItem item3 = new AHBottomNavigationItem(
				"Profile", ContextCompat.getDrawable(this, R.drawable.ic_person_24dp), ContextCompat.getColor(this, R.color.white));

		// Add items
		bottomNavigation.addItem(item1);
		bottomNavigation.addItem(item2);
		bottomNavigation.addItem(item3);

		bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
		bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.primary));
		bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.dark_inactive_icons));

		bottomNavigation.setOnTabSelectedListener(new OnTabSelectedHandler(mPresenter));
		mPresenter.start();
	}


	@Override
	protected void onStart() {
		super.onStart();
		/* this will enter when the user is not using the app and get's a friend request from FCM */
		if (getIntent().getIntExtra(MessageConstants.MESSAGE_TYPE, 0) == MessageConstants.MessageType.FRIEND_REQUEST) {
			User user = getIntent().getParcelableExtra(DatabaseContract.User.TABLE_NAME);
			mPresenter.friendRequest(user);
		}
	}

	@Override
	public void showProfile(User user) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.mainContainer, ProfileFragment.newInstance(user), "profile")
				.commit();
	}


	@Override
	public void setPresenter(MainContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void showChat(boolean show) {
		Fragment fragment = FriendFragment.newInstance();
		getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, fragment).commit();
	}

	@Override
	public void showCamera() {
		Fragment fragment = newInstance();
		getSupportFragmentManager().beginTransaction().add(R.id.mainContainer, fragment, "camera").addToBackStack(null).commit();
	}

	@Override
	public void hideBottomNavigation() {
		findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
	}

	@Override
	public void selfieFragmentDesroyed() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		AHBottomNavigation navigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
		navigation.setVisibility(View.VISIBLE);
//		mPresenter.chatClicked();

	}

	@Override
	public Observable<Bitmap> onButtonClicked() {
		return ((SelfieFragment) getSupportFragmentManager().findFragmentById(R.id.mainContainer)).onButtonClicked();
	}

	@Override
	public void hiddenProfilePictureDetailActions() {
		((SelfieFragment) getSupportFragmentManager().findFragmentByTag("camera")).hiddenDetailPictureActions();
	}

	@Override
	public void pictureTaken(File picture) {
		if (getSupportFragmentManager().findFragmentByTag("profile")!=null) {
			getSupportFragmentManager().popBackStackImmediate(); // pop the camera fragment
			((ProfileFragment) getSupportFragmentManager().findFragmentByTag("profile")).setProfilePicture(picture);
		}
	}

	@Override
	public void showTakenPictureActions() {
		// if we take a picture from Profile fragment, we want to see the profile picture actions to save a new profile
		// picture for tha account
		if (getSupportFragmentManager().findFragmentByTag("profile") != null)
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.mainContainer2, ProfilePictureDetail.newInstance())
					.addToBackStack(null)
					.commit();
		else// the picture is taken with click on camera button
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.mainContainer2, DetailFragment.newInstance())
					.addToBackStack(null)
					.commit();
	}

	/**
	 * This get
	 */
	@Override
	public void hiddenDetailPictureActions() {
		((SelfieFragment) getSupportFragmentManager().findFragmentByTag("camera")).hiddenDetailPictureActions();
	}


	/**
	 * This get's called when the user is in the app and get's a notification(friend request)
	 *
	 * @param message
	 */
	@Override
	public void receiveRemoteMessage(RemoteMessage message) {
		if (message.getNotification().getClickAction().equals(getString(R.string.FRIEND_REQUEST))) {
			User user = new User(message.getData().get(FriendConstants.ID),
					message.getData().get(FriendConstants.NAME),
					message.getData().get(FriendConstants.EMAIL),
					message.getData().get(MessageConstants.FROM), null, null,
					message.getData().get(FriendConstants.ICON_URL),
					message.getData().get(FriendConstants.USERNAME));

			Intent resultIntent = new Intent(this, EntryActivity.class);
			resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

			resultIntent.setAction(getString(R.string.FRIEND_REQUEST));
			resultIntent.putExtra(FriendConstants.ID, user.getUserID());
			resultIntent.putExtra(FriendConstants.NAME, user.getName());
			resultIntent.putExtra(FriendConstants.EMAIL, user.getEmail());
			resultIntent.putExtra(MessageConstants.FROM, user.getToken());
			resultIntent.putExtra(FriendConstants.ICON_URL, user.getIconUrl());
			resultIntent.putExtra(FriendConstants.USERNAME, user.getUsername());

			PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationCompat.Builder mBuilder = new NotificationCompat
					.Builder(this)
					.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | FLAG_AUTO_CANCEL)
					.setAutoCancel(true)
					.setSmallIcon(R.drawable.pixsee_v2)
					.setContentTitle(message.getNotification().getTitle())
					.setContentText(message.getNotification().getBody());
			mBuilder.setContentIntent(resultPendingIntent);

			// Gets an instance of the NotificationManager service
			NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// Builds the notification and issues it.
			mNotifyMgr.notify(1, mBuilder.build());

			Log.d("*** TAG ***", "receiveRemoteMessage: " + message.toString());
		}
	}

	@Override
	public void showFriendRequestDialog(final User user) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setCancelable(false)
				.setTitle("Friend Request")
				.setMessage(String.format("Accept friend request from %s?", user.getName()));

		builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mPresenter.friendRequest(user, true);
				finish();
			}
		});
		builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mPresenter.friendRequest(user, false);
				finish();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onFriendClick(User friend) {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(ChatActivity.EXTRA_CONTACT, friend);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		GCMListenerService.clear();
		super.onDestroy();
	}

	@Override
	public void onCameraClick() {
		mPresenter.cameraClicked();
	}

	private static class OnTabSelectedHandler implements AHBottomNavigation.OnTabSelectedListener {
		private WeakReference<MainContract.Presenter> mPresenterWeakReference;

		public OnTabSelectedHandler(MainContract.Presenter presenterWeakReference) {
			mPresenterWeakReference = new WeakReference<MainContract.Presenter>(presenterWeakReference);
		}

		@Override
		public void onTabSelected(int position, boolean wasSelected) {
			switch (position) {
				case 0:
					if (!wasSelected)
						mPresenterWeakReference.get().chatClicked();
					break;
				case 1:
					if (!wasSelected)
						mPresenterWeakReference.get().cameraClicked();
					break;
				case 2:
					if (!wasSelected)
						mPresenterWeakReference.get().profileClicked();
					break;
			}
		}
	}
}
