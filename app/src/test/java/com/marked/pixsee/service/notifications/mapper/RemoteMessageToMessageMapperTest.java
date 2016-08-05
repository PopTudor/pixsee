package com.marked.pixsee.service.notifications.mapper;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.chat.data.MessageConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.marked.pixsee.chat.data.MessageConstants.DATA_BODY;
import static org.hamcrest.Matchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Tudor on 01-Aug-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RemoteMessageToMessageMapperTest {
	private static final String BODY = "body";
	private static final String TO = "to";
	private static final String FROM = "from";
	private static final int MESSAGE_TYPE = 2;
	private RemoteMessage mRemoteMessage;
	private RemoteMessageToMessageMapper mRemoteMessageToMessageMapper;
	private Message actualMessage;

	@Before
	public void setUp() throws Exception {
		mRemoteMessageToMessageMapper = new RemoteMessageToMessageMapper();
		mRemoteMessage = new RemoteMessage.Builder("1")
				.addData(DATA_BODY, BODY)
				.addData(MessageConstants.TO, TO)
				.addData(MessageConstants.FROM, FROM)
				.addData(MessageConstants.MESSAGE_TYPE, String.valueOf(MESSAGE_TYPE))
				.build();
		actualMessage = mRemoteMessageToMessageMapper.map(mRemoteMessage);
	}

	@Test
	public void testMap_shouldCreateMessage() throws Exception {
		assertThat(actualMessage, any(Message.class));
	}

	@Test
	public void testMap_shouldReturnData() throws Exception {
		assertEquals(mRemoteMessage.getData().get(DATA_BODY), actualMessage.getData().get(BODY));
		assertEquals(Integer.parseInt(mRemoteMessage.getData().get(MessageConstants.MESSAGE_TYPE)), actualMessage.getMessageType().intValue());
		assertEquals(String.valueOf(mRemoteMessage.getSentTime()), actualMessage.getDate());
	}

	@Test
	public void testMap_shouldReturnParameters() throws Exception {
		assertEquals(TO, actualMessage.getTo());
		assertEquals(FROM, actualMessage.getFrom());
		assertEquals(MESSAGE_TYPE, actualMessage.getMessageType().longValue());
	}

	@Test
	public void missingMessageType_shouldBeZero() throws Exception {
		mRemoteMessage = new RemoteMessage.Builder("1").build();

		Message actual = mRemoteMessageToMessageMapper.map(mRemoteMessage);

		assertEquals(0,actual.getMessageType().intValue());
	}
}