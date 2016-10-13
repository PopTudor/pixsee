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
import com.marked.pixsee.di.modules.FakeActivityModule;
import com.marked.pixsee.friends.FriendFragment;
import com.marked.pixsee.model.user.User;
import com.marked.pixsee.profile.ProfileFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Tudor on 21-Jul-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class MainActivityTest {
	private User mUser = UserUtilTest.getUserTest();
	ActivityController<MainActivity> mMainActivity;

	@Before
	public void setUp() throws Exception {
		mMainActivity = Robolectric.buildActivity(MainActivity.class);
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
	public void testNewFriendRequest_ShouldCallPresenterFriendRequest() throws Exception {
		User user = UserUtilTest.getUserTest();
		Intent intent = Mockito.mock(Intent.class);
		Mockito.doReturn(user).when(intent).getParcelableExtra(Matchers.anyString());

		mMainActivity.create().start().get().newFriendRequest(intent);
		// if it's a friend request, onStart will call the presenter's friendRequest with given user
		Mockito.verify(mMainActivity.get().mPresenter).friendRequest(user);
	}

	@Test
	public void testNewFriendRequest_ShouldNotCallPresenterFriendRequest() throws Exception {
		Intent intent = Mockito.mock(Intent.class);
//		Mockito.doReturn(null).when(intent).getParcelableExtra(Matchers.anyString());
		mMainActivity.create().get().newFriendRequest(intent);
		// if it's a friend request, onStart will call the presenter's friendRequest with given user
		Mockito.verify(mMainActivity.get().mPresenter,Mockito.never()).friendRequest(Mockito.<User>any());
	}


	@Test
	public void testOnStart_ShouldNotShowFriendRequestDialog() throws Exception {
		User user = UserUtilTest.getUserTest();
		mMainActivity.create();
		mMainActivity.get().newFriendRequest(Mockito.mock(Intent.class));
		// if it's a friend request, onStart will call the presenter's friendRequest with given user
		Mockito.verify(mMainActivity.get().mPresenter,Mockito.never()).friendRequest(user);

	}

	@Test
	public void testOnNewIntent() throws Exception {
		User user = UserUtilTest.getUserTest();
		Intent intent = Mockito.mock(Intent.class);
		Mockito.doReturn(user).when(intent).getParcelableExtra(Matchers.anyString());

		mMainActivity.create().start().resume().newIntent(intent);
		Mockito.verify(mMainActivity.get().mPresenter).friendRequest(user);
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
	public void testShowFriendRequestDialog_ShouldAcceptFriendship() throws Exception {
		MainContract.View view = mMainActivity.create().get();
		AlertDialog dialog = view.showFriendRequestDialog(mUser);
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
		// accept friend request
		Mockito.verify(mMainActivity.get().mPresenter).friendRequest(mUser, true);
		// close activity
		Shadows.shadowOf(mMainActivity.get()).isFinishing();
	}

	@Test
	public void testShowFriendRequestDialog_ShouldDeclineFriendship() throws Exception {
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