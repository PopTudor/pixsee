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

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.R;
import com.marked.pixsee.chat.ChatActivity;
import com.marked.pixsee.chat.GCMListenerService;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.entry.EntryActivity;
import com.marked.pixsee.friends.FriendFragment;
import com.marked.pixsee.friends.data.FriendConstants;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.main.strategy.ProfilePictureStrategy;
import com.marked.pixsee.profile.ProfileFragment;
import com.marked.pixsee.main.strategy.PictureActionStrategy;
import com.marked.pixsee.selfie.PictureDetailSendFragment;
import com.marked.pixsee.selfie.PictureDetailShareFragment;
import com.marked.pixsee.selfie.SelfieFragment;

import org.jetbrains.annotations.NotNull;

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
		PictureDetailShareFragment.OnPictureDetailShareListener, OnSelfieInteractionListener,
		PictureDetailSendFragment.OnPictureDetailSendListener ,ProfileFragment.ProfileFragmentInteraction{
	public static final int START_CAMERA_REQUEST_CODE = 100;
	@Inject
	MainContract.Presenter mPresenter;
	private AHBottomNavigation mBottomNavigation;
	private PictureActionStrategy mPictureActionStrategy;

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
	protected void onStart() {
		super.onStart();
		/* this will enter when the user is not using the app and get's a friend request from FCM */
		if (getIntent().getIntExtra(MessageConstants.MESSAGE_TYPE, 0) == MessageConstants.MessageType.FRIEND_REQUEST) {
			User user = getIntent().getParcelableExtra(DatabaseContract.AppsUser.TABLE_NAME);
			mPresenter.friendRequest(user);
		}
	}

	@Override
	public void setPresenter(MainContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void showProfile(User user) {
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
		Fragment fragment = newInstance();
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

	/*************************** OnFriendFragmentInteractionListener *************************/
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

	/*************************** OnProfilePictureInteraction *************************/
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
