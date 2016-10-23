package com.marked.pixsee.ui.notifications;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.R;
import com.marked.pixsee.ui.chat.data.MessageConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.marked.pixsee.ui.notifications.FcmNotificationFactoryImpl.InvalidNotificationException;
import static org.hamcrest.Matchers.instanceOf;
import static org.robolectric.RuntimeEnvironment.application;

/**
 * Created by Tudor on 01-Aug-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class FcmNotificationFactoryImplTest {
    private static final String CLICK_ACTION = "click_action";
    private String FRIEND_REQUEST_ACTION;
    private String NEW_MESSAGE_ACTION;
    private String INVALID_ACTION;

    private FcmNotificationFactoryImpl fcmNotificationFactory;

    public FcmNotificationFactoryImplTest() {
        FRIEND_REQUEST_ACTION = application.getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION);
        NEW_MESSAGE_ACTION = application.getString(R.string.NEW_MESSAGE_NOTIFICATION_ACTION);
        INVALID_ACTION = "invalid.action";
    }

    @Before
    public void setUp() throws Exception {

        fcmNotificationFactory = new FcmNotificationFactoryImpl(application);
    }

    @Test
    public void testCreateNotification_shouldCreateFriendRequestNotification() throws Exception {
        RemoteMessage remoteMessage = createRemoteMessage(FRIEND_REQUEST_ACTION);

        FcmNotification notification = fcmNotificationFactory.createNotification(remoteMessage);

        Assert.assertThat(notification, instanceOf(FriendRequestNotification.class));
    }

    @Test
    public void testCreateNotification_shouldCreateNewMessageNotification() throws Exception {
        RemoteMessage remoteMessage = createRemoteMessage(NEW_MESSAGE_ACTION);

        FcmNotification notification = fcmNotificationFactory.createNotification(remoteMessage);

        Assert.assertThat(notification, instanceOf(NewMessageNotification.class));
    }

    @Test
    public void testCreateNotification_shouldCreateEmptyNotification() throws Exception {
        try {
            RemoteMessage remoteMessage = createRemoteMessage(INVALID_ACTION);

            fcmNotificationFactory.createNotification(remoteMessage);
        } catch (RuntimeException e) {
            Assert.assertThat(e, instanceOf(InvalidNotificationException.class));
        }
    }

    private RemoteMessage createRemoteMessage(String clickAction) {
        return new RemoteMessage.Builder("abc")
                .addData(MessageConstants.MESSAGE_TYPE, String.valueOf("2"))
                .addData(CLICK_ACTION, clickAction)
		               .addData(MessageConstants.TO, "abc")
		               .addData(MessageConstants.FROM, "abc")
		               .build();
    }
}