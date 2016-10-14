package com.marked.pixsee.service.notifications;

import android.app.Notification;
import android.content.ComponentName;
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
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.RuntimeEnvironment.application;

/**
 * Created by Tudor on 30-Jul-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class FriendRequestNotificationTest {
    private User mUser;
    private String FRIEND_REQUEST_TAG;
    private Notification actualNotification;
    private Intent actualIntent;
	@Mock
	Mapper<RemoteMessage, User> mRemoteMessageUserMapper;

    public FriendRequestNotificationTest() {
        FRIEND_REQUEST_TAG = application.getString(R.string.FRIEND_REQUEST_NOTIFICATION_ACTION);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mUser = UserUtilTest.getUserTest();

        FriendRequestNotification friendRequestNotification = new FriendRequestNotification(application, mUser);
        actualIntent = friendRequestNotification.createIntent();
        actualNotification = friendRequestNotification.buildNotification();
    }

    @Test
    public void testIntentNotNull() throws Exception {
        assertNotNull(actualIntent);
    }

    @Test
    public void testNotificationNotNull() throws Exception {
        assertNotNull(actualNotification);
    }

    @Test
    public void testIntent_startMainActivity() throws Exception {
        ComponentName componentName = actualIntent.getComponent();
        String className = componentName.getClassName();

        assertEquals(MainActivity.class.getName(), className);
    }

    @Test
    public void testBuildNotification_ShouldBeEqual() throws Exception {
        Intent intent = new Intent(application, MainActivity.class);
        intent.setAction(FRIEND_REQUEST_TAG);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addCategory(Notification.CATEGORY_SOCIAL);
        intent.putExtra(FRIEND_REQUEST_TAG, mUser);

        assertTrue(actualIntent.filterEquals(intent));
    }
}