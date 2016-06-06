package com.marked.pixsee.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.R;
import com.marked.pixsee.chat.ChatActivity;
import com.marked.pixsee.chat.GCMListenerService;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.entry.EntryActivity;
import com.marked.pixsee.friends.FriendFragment;
import com.marked.pixsee.friends.data.FriendConstants;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.main.commands.SelfieCommand;
import com.marked.pixsee.main.di.DaggerMainComponent;
import com.marked.pixsee.main.di.MainModule;
import com.marked.pixsee.profile.ProfileFragment;

import javax.inject.Inject;

import static android.support.v4.app.NotificationCompat.FLAG_AUTO_CANCEL;

public class MainActivity extends AppCompatActivity implements MainContract.View, FriendFragment
.FriendFragmentInteractionListener, GCMListenerService.Callback {
	@Inject
	MainContract.Presenter mPresenter;

	private FrameLayout mainContainer;
	private ImageButton mChatImageButton;
	private ImageButton mSelfieImageButton;
	private ImageButton mProfileImageButton;


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
		mainContainer = (FrameLayout) findViewById(R.id.mainContainer);
		mChatImageButton = (ImageButton) findViewById(R.id.chatImageButton);
		mSelfieImageButton = (ImageButton) findViewById(R.id.selfieImageButton);
		mProfileImageButton = (ImageButton) findViewById(R.id.profileImageButton);

		mChatImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.chatClicked();
			}
		});
		mSelfieImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.execute(new SelfieCommand(MainActivity.this));
			}
		});
		mProfileImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.profileClicked();
			}
		});
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
				.replace(R.id.mainContainer, ProfileFragment.newInstance(user))
				.commit();
	}

	@Override
	public void setPresenter(MainContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void displayChat(boolean show) {
		Fragment fragment = FriendFragment.newInstance();
		getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, fragment).commit();
	}

	@Override
	public void onFriendClick(User friend) {

		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(ChatActivity.EXTRA_CONTACT, friend);
		startActivity(intent);
//		CardFragment fragment = CardFragment.newInstance(friend);
//		CardLocalDatasource localDatasource = new CardLocalDatasource(PixyDatabase.getInstance(this));
//		CardRemoteDatasource remoteDatasource = new CardRemoteDatasource(PreferenceManager.getDefaultSharedPreferences(this));
//		fragment.setPresenter(new CardPresenter(fragment, new CardRepository(localDatasource,remoteDatasource)));
//		getSupportFragmentManager().beginTransaction().replace(R.id.cardFragmentContainer, fragment).commit();
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

			PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationCompat.Builder mBuilder = new NotificationCompat
					.Builder(this)
					.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|FLAG_AUTO_CANCEL)
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
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getIntExtra(MessageConstants.MESSAGE_TYPE, 0) == MessageConstants.MessageType.FRIEND_REQUEST) {
			User user = getIntent().getParcelableExtra(DatabaseContract.User.TABLE_NAME);
			mPresenter.friendRequest(user);
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
	protected void onDestroy() {
		GCMListenerService.clear();
		super.onDestroy();
	}

}
