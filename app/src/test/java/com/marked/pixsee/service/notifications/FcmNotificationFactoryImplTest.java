package com.marked.pixsee.service.notifications;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.instanceOf;

/**
 * Created by Tudor on 01-Aug-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class FcmNotificationFactoryImplTest {
	Context mContext;

	private String FRIEND_REQUEST;
	private String NEW_MESSAGE;

	@Before
	public void setUp() throws Exception {
		mContext = RuntimeEnvironment.application;
		FRIEND_REQUEST = mContext.getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION);
		NEW_MESSAGE = mContext.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION);
	}

	@Test
	public void testCreateNotification_shouldCreateFriendRequestNotification() throws Exception {
		RemoteMessage remoteMessage = new RemoteMessage.Builder("Pasd").addData("click_action", FRIEND_REQUEST).build();

		FcmNotificationFactoryImpl fcmNotificationFactory = new FcmNotificationFactoryImpl(mContext);
		FcmNotification notification = fcmNotificationFactory.createNotification(remoteMessage);
		Assert.assertThat(notification, instanceOf(FriendRequestNotification.class));
	}

	@Test
	public void testCreateNotification_shouldCreateNewMessageNotification() throws Exception {
		RemoteMessage remoteMessage = new RemoteMessage.Builder("Pasd").addData("click_action", NEW_MESSAGE).build();

		FcmNotificationFactoryImpl fcmNotificationFactory = new FcmNotificationFactoryImpl(mContext);
		FcmNotification notification = fcmNotificationFactory.createNotification(remoteMessage);
		Assert.assertThat(notification, instanceOf(NewMessageNotification.class));
	}

	@Test
	public void testCreateNotification_shouldCreateEmptyNotification() throws Exception {
		RemoteMessage remoteMessage = new RemoteMessage.Builder("Pasd").addData("click_action", "").build();

		FcmNotificationFactoryImpl fcmNotificationFactory = new FcmNotificationFactoryImpl(mContext);
		FcmNotification notification = fcmNotificationFactory.createNotification(remoteMessage);
		Assert.assertThat(notification, instanceOf(EmptyNotification.class));
	}
}