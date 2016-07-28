package com.marked.pixsee.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.R;
import com.marked.pixsee.UserUtilTest;
import com.marked.pixsee.chat.ChatActivity;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.entry.EntryActivity;
import com.marked.pixsee.friends.FriendFragment;
import com.marked.pixsee.injection.modules.FakeActivityModule;
import com.marked.pixsee.main.strategy.PictureActionStrategy;
import com.marked.pixsee.main.strategy.ShareStrategy;
import com.marked.pixsee.profile.ProfileFragment;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by Tudor on 21-Jul-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class MainActivityTest {
	ActivityController<MainActivityTest.MainActivity> mMainActivity;
	private User mUser = UserUtilTest.getUserTest();

	@Before
	public void setUp() throws Exception {
		mMainActivity = Robolectric.buildActivity(MainActivityTest.MainActivity.class);
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
		Robolectric.reset();
	}

	@Test
	public void testOnCreate() throws Exception {
		mMainActivity.create();
		Mockito.verify(mMainActivity.get().mPresenter).attach();
	}

	@Test
	public void testOnStartShouldShowFriendRequestDialog() throws Exception {
		User user = UserUtilTest.getUserTest();
		Intent intent = new Intent();
		intent.putExtra(MessageConstants.MESSAGE_TYPE, MessageConstants.MessageType.FRIEND_REQUEST);
		intent.putExtra(DatabaseContract.AppsUser.TABLE_NAME, user);
		// start activity
		MainActivity activity = Robolectric.buildActivity(MainActivity.class).withIntent(intent).create().start().get();
		// if it's a friend request, onStart will call the presenter's friendRequest with given user
		Mockito.verify(activity.mPresenter).friendRequest(user);
		// check if the dialog is showing
		assertThat(activity.showFriendRequestDialog(user).isShowing(), CoreMatchers.any(Boolean.class));
	}

	@Test
	public void testFriendRequestEventShouldStartEntryActivity() throws Exception {
		mMainActivity.create();
		//emulate friendRequestEvent Intent
		Intent intent = new Intent(mMainActivity.get(), EntryActivity.class);

		FriendRequestEvent friendRequestEvent = Mockito.mock(FriendRequestEvent.class);
		Mockito.doReturn(intent).when(friendRequestEvent).buildIntent(mMainActivity.get());
		mMainActivity.get().friendRequestEvent(friendRequestEvent);

		Intent startingIntent = Shadows.shadowOf(mMainActivity.get()).getNextStartedActivity();
		assertEquals(intent, startingIntent);
	}

	@Test
	public void testHideBottomNavigation() throws Exception {
		mMainActivity.create().start().resume().visible();
		mMainActivity.get().hideBottomNavigation();
		View view = mMainActivity.get().findViewById(R.id.bottom_navigation);
		assertNotNull(view);
		assertEquals(view.getVisibility(), View.GONE);
	}

	@Test
	public void testSelfieFragmentDesroyed() throws Exception {
		mMainActivity.create().start().resume().visible();
		mMainActivity.get().selfieFragmentDesroyed();
		View view = mMainActivity.get().findViewById(R.id.bottom_navigation);
		assertNotNull(view);
		assertEquals(view.getVisibility(), View.VISIBLE);
	}

	@Test
	public void testShowProfile() throws Exception {
		mMainActivity.create().start().resume().visible().get().showProfile(UserUtilTest.getUserTest());
		Fragment fragment = mMainActivity.get().getSupportFragmentManager().findFragmentById(R.id.mainContainer);
		assertTrue(fragment instanceof ProfileFragment);
	}

	@Test
	public void testShowChat() throws Exception {
		mMainActivity.create().start().resume().visible().get().showChat(true);
		Fragment fragment = mMainActivity.get().getSupportFragmentManager().findFragmentById(R.id.mainContainer);
		assertTrue(fragment instanceof FriendFragment);
	}

	@Test
	public void testShowFriendRequestDialogShouldAcceptFriendship() throws Exception {
		MainContract.View view = mMainActivity.create().get();
		AlertDialog dialog = view.showFriendRequestDialog(mUser);
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
		// accept friend request
		Mockito.verify(mMainActivity.get().mPresenter).friendRequest(mUser, true);
		// close activity
		Shadows.shadowOf(mMainActivity.get()).isFinishing();
	}

	@Test
	public void testShowFriendRequestDialogShouldDeclineFriendship() throws Exception {
		MainContract.View view = mMainActivity.create().get();
		AlertDialog dialog = view.showFriendRequestDialog(mUser);
		dialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();
		// reject friend request
		Mockito.verify(mMainActivity.get().mPresenter).friendRequest(mUser, false);
		// close activity
		Shadows.shadowOf(mMainActivity.get()).isFinishing();
	}

	@Test
	public void testOnFriendClick() throws Exception {
		mMainActivity.get().onFriendClick(mUser);
		Intent expectedIntent = new Intent(mMainActivity.get(), ChatActivity.class);
		expectedIntent.putExtra(ChatActivity.EXTRA_CONTACT, mUser);

		Intent intent = Shadows.shadowOf(mMainActivity.get()).getNextStartedActivity();//
		assertEquals("Starting intent and resulting activity's intent should be the same",//
				intent.getParcelableExtra(ChatActivity.EXTRA_CONTACT), mUser);
	}

	@Test
	public void testOnTakeProfilePictureClick() throws Exception {
		mMainActivity.create().get().onTakeProfilePictureClick();
		Mockito.verify(mMainActivity.get().mPresenter).profileImageClicked();
	}

	@Test
	public void testShowTakenPictureActions() throws Exception {
		PictureActionStrategy pictureActionStrategy = Mockito.mock(ShareStrategy.class);
		mMainActivity.get().setPictureActionStrategy(pictureActionStrategy);
		mMainActivity.get().showTakenPictureActions();
		Mockito.verify(pictureActionStrategy).showAction(mMainActivity.get());
	}


	private static class MainActivity extends com.marked.pixsee.main.MainActivity {
		@Override
		public void injectComponent() {
			DaggerFakeMainComponent.builder()
					.fakeActivityModule(new FakeActivityModule(this))
					.fakeMainModule(new FakeMainModule())
					.build()
					.inject(this);
		}
	}
}