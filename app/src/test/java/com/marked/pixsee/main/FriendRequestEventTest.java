package com.marked.pixsee.main;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.R;
import com.marked.pixsee.UserUtilTest;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.entry.EntryActivity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by Tudor on 27-Jul-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class FriendRequestEventTest {
	@Test
	public void testBuildIntent() throws Exception {
		User user = UserUtilTest.getUserTest();
		Activity activity = Robolectric.buildActivity(MainActivity.class).get();

		RemoteMessageToUserMapper remoteMessageToUserMapper = mock(RemoteMessageToUserMapper.class);
		doReturn(user).when(remoteMessageToUserMapper).map(Matchers.any(RemoteMessage.class));

		FriendRequestEvent friendRequestEvent = new FriendRequestEvent(Mockito.any(RemoteMessage.class),remoteMessageToUserMapper);

		Intent actual = friendRequestEvent.buildIntent(activity);


		Intent expected = new Intent(activity, EntryActivity.class);
		expected.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		expected.setAction(activity.getString(R.string.FRIEND_REQUEST));
		expected.putExtra(User.TAG, user);

		Assert.assertTrue(expected.filterEquals(actual));
		Assert.assertEquals(expected.getExtras().toString(),actual.getExtras().toString());
	}
}