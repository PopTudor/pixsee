package com.marked.pixsee.service;

import com.marked.pixsee.service.notifications.FcmNotificationFactoryImplTest;
import com.marked.pixsee.service.notifications.FriendRequestNotificationTest;
import com.marked.pixsee.service.notifications.NewMessageNotificationTest;
import com.marked.pixsee.service.notifications.mapper.RemoteMessageToMessageMapperTest;
import com.marked.pixsee.service.notifications.mapper.RemoteMessageToUserMapperTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Tudor on 01-Aug-16.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
		RemoteMessageToUserMapperTest.class,
		RemoteMessageToMessageMapperTest.class,
		FriendRequestNotificationTest.class,
		FcmNotificationFactoryImplTest.class,
		NewMessageNotificationTest.class,
		GCMInstanceIDListenerServiceTest.class,
		GCMListenerServiceTest.class
})
public class ServiceTestSuite {
}
