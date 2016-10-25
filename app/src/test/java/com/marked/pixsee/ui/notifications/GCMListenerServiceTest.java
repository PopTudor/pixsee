package com.marked.pixsee.ui.notifications;

import android.app.NotificationManager;
import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowNotificationManager;
import org.robolectric.shadows.ShadowService;

import static com.marked.pixsee.utils.RemoteMessage.createRemoteMessage;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by Tudor on 03-Aug-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GCMListenerServiceTest {
	private final ShadowNotificationManager mShadowNotificationManager =
			shadowOf((NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE));
	private GCMListenerService mGCMListenerService;
	private ShadowService shadowService;
	private RemoteMessage mRemoteMessage;

	@Before
	public void setUp() throws Exception {
		mGCMListenerService = Robolectric.setupService(GCMListenerService.class);

		shadowService = shadowOf(mGCMListenerService);
	}

	@Test
	public void testFriendRequestNotification_shouldBePostedToNotificationManager() throws Exception {
		mRemoteMessage = createRemoteMessage(application.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION));

		mGCMListenerService.onMessageReceived(mRemoteMessage);

		Assert.assertEquals(1, mShadowNotificationManager.size());
	}

	@Test
	public void testNewMessageNotification_shouldBePostedToNotificationManager() throws Exception {
		mRemoteMessage = createRemoteMessage(application.getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION));

		mGCMListenerService.onMessageReceived(mRemoteMessage);

		Assert.assertEquals(1, mShadowNotificationManager.size());
	}
}