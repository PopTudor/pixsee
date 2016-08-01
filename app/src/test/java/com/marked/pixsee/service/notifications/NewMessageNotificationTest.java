package com.marked.pixsee.service.notifications;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.R;
import com.marked.pixsee.chat.ChatActivity;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.data.Mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPendingIntent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Tudor on 01-Aug-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NewMessageNotificationTest {
	private RemoteMessage mRemoteMessage;
	private Context mContext;
	@Mock
	private Mapper<RemoteMessage, Message> mMapper;
	private NewMessageNotification newMessageNotification;
	private Message mMessage;
	private Notification actual;


	@Before
	public void setUp() throws Exception {
		mRemoteMessage = new RemoteMessage.Builder("123")
				.addData(MessageConstants.DATA_BODY, MessageConstants.DATA_BODY)
				.addData(MessageConstants.MESSAGE_TYPE, String.valueOf(2))
				.addData(MessageConstants.CREATION_DATE, "123")
				.build();
		mMessage = new Message.Builder()
				.addData(MessageConstants.DATA_BODY, MessageConstants.DATA_BODY)
				.messageType(2)
				.date("123")
				.build();

		mContext = RuntimeEnvironment.application;
		MockitoAnnotations.initMocks(this);

		Mockito.when(mMapper.map(mRemoteMessage)).thenReturn(mMessage);

		newMessageNotification = new NewMessageNotification(mRemoteMessage, mMapper, mContext);
		actual = newMessageNotification.buildNotification();
	}

	@Test
	public void testBuildNotification_NotNull() throws Exception {
		assertNotNull(actual);
	}

	@Test
	public void testIntent_shouldNotBeNull() throws Exception {
		assertNotNull("No pending intent has been set to the notification !", actual.contentIntent);
	}

	@Test
	public void testIntent_shouldStartChatActivity() throws Exception {
		ShadowPendingIntent pendingIntent = Shadows.shadowOf(actual.contentIntent);
		assertEquals(pendingIntent.getSavedIntent().getComponent().getClassName(), ChatActivity.class.getName());
	}

	@Test
	public void testIntent_shouldHaveRemoteMessageData() throws Exception {
		ShadowPendingIntent pendingIntent = Shadows.shadowOf(actual.contentIntent);
		Intent intent = pendingIntent.getSavedIntent();
		Message actual = intent.getParcelableExtra(mContext.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION));
		/* remote message data == message data*/
		assertEquals(mRemoteMessage.getData().get(MessageConstants.DATA_BODY), actual.getData().get(MessageConstants.DATA_BODY));
		assertEquals(Integer.parseInt(mRemoteMessage.getData().get(MessageConstants.MESSAGE_TYPE)), actual.getMessageType().intValue());
		assertEquals(mRemoteMessage.getData().get(MessageConstants.CREATION_DATE), actual.getDate());
	}

	@Test
	public void testIntent_shouldHaveSameActionsGroups() throws Exception {
		Intent expectedIntent = new Intent(mContext, ChatActivity.class);
		expectedIntent.setAction(mContext.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION));

		ShadowPendingIntent actualPendintIntent = Shadows.shadowOf(actual.contentIntent);
		Intent actualIntent = actualPendintIntent.getSavedIntent();
		assertTrue(expectedIntent.filterEquals(actualIntent));
	}
}