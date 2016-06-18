package com.marked.pixsee.chat;

import com.marked.pixsee.chat.data.ChatRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by Tudor on 18-Jun-16.
 */
public class ChatPresenterTest {
	@Mock
	ChatContract.View mView;
	@Mock
	ChatRepository mChatRepository;

	ChatPresenter mPresenter;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mPresenter = new ChatPresenter(mView, mChatRepository);
	}

	@Test
	public void testStart() throws Exception {

	}

	@Test
	public void testLoadMore() throws Exception {

	}

	@Test
	public void testLoadMore1() throws Exception {

	}

	@Test
	public void testReceiveRemoteMessage() throws Exception {

	}

	@Test
	public void testReceiveMessage() throws Exception {

	}

	@Test
	public void testSendMessage() throws Exception {

	}

	@Test
	public void testIsTyping() throws Exception {

	}

	@Test
	public void testChatClicked() throws Exception {

	}

	@Test
	public void testFiltersButtonClicked() throws Exception {

	}

	@Test
	public void testTakePicture() throws Exception {
		mPresenter.onCameraClick();
//		Mockito.verify(mView).
	}

	@Test
	public void testLoadMore2() throws Exception {

	}

	@Test
	public void testExecute() throws Exception {

	}

}