package com.marked.pixsee.service.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.R;
import com.marked.pixsee.UserUtilTest;
import com.marked.pixsee.data.Mapper;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.main.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPendingIntent;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Tudor on 30-Jul-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class FriendRequestNotificationTest {
	Context mContext;
	User mUser;
	RemoteMessage remoteMessage;
		Intent intent;

	@Mock
	Mapper<RemoteMessage, User> mRemoteMessageUserMapper;
	private String FRIEND_REQUEST_TAG;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mContext = RuntimeEnvironment.application;

		FRIEND_REQUEST_TAG = mContext.getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION);
		mUser = UserUtilTest.getUserTest();
		remoteMessage = new RemoteMessage.Builder("asd").build();

		intent = new Intent(mContext, MainActivity.class);
		intent.setAction(FRIEND_REQUEST_TAG);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addCategory(Notification.CATEGORY_SOCIAL);
		intent.putExtra(FRIEND_REQUEST_TAG, mUser);
	}

	@Test
	public void testBuildNotification_ShouldBeEqual() throws Exception {
		doReturn(mUser).when(mRemoteMessageUserMapper).map(remoteMessage);

		FriendRequestNotification friendRequestNotification =
				new FriendRequestNotification(remoteMessage, mRemoteMessageUserMapper, mContext);

		Notification notification = friendRequestNotification.buildNotification();
		ShadowPendingIntent shadowPendingIntent = Shadows.shadowOf(notification.contentIntent);
		assertTrue(shadowPendingIntent.getSavedIntent().filterEquals(intent));
	}
}