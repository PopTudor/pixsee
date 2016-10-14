package com.marked.pixsee.service.notifications;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;

import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.R;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.features.chat.ChatActivity;
import com.marked.pixsee.features.chat.data.MessageConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.RuntimeEnvironment.application;

/**
 * Created by Tudor on 01-Aug-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NewMessageNotificationTest {
    private Message mMessage;
    private Notification actualNotification;
    private Intent actualIntent;

    public NewMessageNotificationTest() {
    }

    @Before
    public void setUp() throws Exception {
        mMessage = new Message.Builder()
                .addData(MessageConstants.DATA_BODY, MessageConstants.DATA_BODY)
                .messageType(2)
                .date("123")
                .build();
        MockitoAnnotations.initMocks(this);

        NewMessageNotification newMessageNotification = new NewMessageNotification(application, mMessage);

        actualIntent = newMessageNotification.createIntent();
        actualNotification = newMessageNotification.buildNotification();
    }

    @Test
    public void testNotification_NotNull() throws Exception {
        assertNotNull(actualNotification);
    }

    @Test
    public void testIntent_NotNull() throws Exception {
        assertNotNull("No pending intent has been set to the notification !", actualNotification.contentIntent);
    }

    @Test
    public void testIntent_startChatActivity() throws Exception {
        ComponentName componentName = actualIntent.getComponent();
        String className = componentName.getClassName();
        assertEquals(ChatActivity.class.getName(), className);
    }

    @Test
    public void testIntent_haveRemoteMessageData() throws Exception {
        Message actual = actualIntent.getParcelableExtra(application.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION));
        /* remote message data == message data*/
        assertEquals(mMessage.getData().get(MessageConstants.DATA_BODY), actual.getData().get(MessageConstants.DATA_BODY));
        assertEquals(mMessage.getMessageType().intValue(), actual.getMessageType().intValue());
        assertEquals(mMessage.getDate(), actual.getDate());
    }

    @Test
    public void testIntent_shouldHaveSameActionsGroups() throws Exception {
        Intent expectedIntent = new Intent(application, ChatActivity.class);
        expectedIntent.setAction(application.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION));
        expectedIntent.addCategory(Notification.CATEGORY_MESSAGE);

        assertTrue(expectedIntent.filterEquals(actualIntent));
    }
}