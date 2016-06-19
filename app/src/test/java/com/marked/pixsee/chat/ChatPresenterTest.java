package com.marked.pixsee.chat;

import android.os.Bundle;

import com.marked.pixsee.TestSchedulerProxy;
import com.marked.pixsee.chat.data.ChatRepository;
import com.marked.pixsee.chat.data.Message;
import com.marked.pixsee.data.repository.user.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.List;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Tudor on 18-Jun-16.
 */
public class ChatPresenterTest {
	@Mock
	ChatContract.View mView;
	@Mock
	ChatRepository mChatRepository;

	@Mock
	ChatFragment.ChatFragmentInteraction mChatFragmentInteraction;

	ChatPresenter mPresenter;
	List<Message> mExpectedMessages = MessageTestUtils.getRandomMessageList(3);


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mPresenter = new ChatPresenter(mView, mChatRepository,new User("abc","abc","abc","abc"));
		mPresenter.setChatInteraction(mChatFragmentInteraction);
		TestSchedulerProxy.get();
	}

	@Test
	public void testLoadMoreByUserIdSuccess() throws Exception {
		when(mChatRepository.getMessages(any(User.class)))
				.thenReturn(Observable.just(mExpectedMessages));

		mPresenter.loadMore(50, any(User.class));

		verify(mChatRepository).getMessages(any(User.class));
		verify(mView).showCards(mExpectedMessages);
	}

	@Test
	public void testLoadMoreByUserIdError() throws Exception {
		when(mChatRepository.getMessages(any(User.class)))
				.thenReturn(Observable.<List<Message>>error(new Error()));

		mPresenter.loadMore(50, any(User.class));

		verify(mChatRepository).getMessages(any(User.class));
		verify(mView).showNoChats();
	}

	@Test
	public void testReceiveMessage() throws Exception {
		Message message = MessageTestUtils.getRandomMessage();
		Bundle bundle = mock(Bundle.class);
		mPresenter.receiveMessage("abc",bundle);
		verify(mChatRepository).saveMessage(any(Message.class));
		verify(mView).addMessage(any(Message.class));
	}

	@Test
	public void testSendMessage() throws Exception {
		Message message = MessageTestUtils.getRandomMessage();
		mPresenter.sendMessage(message);
		verify(mChatRepository).saveMessage(message);
		verify(mView).addMessage(message);
	}

	@Test
	public void testIsTypingTrue() throws Exception {
		mPresenter.isTyping(true);
		verify(mView).addMessage(any(Message.class));
	}

	@Test
	public void testIsTypingFalse() throws Exception {
		mPresenter.isTyping(false);
		verify(mView).pop();
	}

	/**
	 * Check if a message gets deleted from repository and from the screen on click
	 * @throws Exception
	 */
	@Test
	public void testChatClicked() throws Exception {
		Message message = MessageTestUtils.getRandomMessage();
		mPresenter.chatClicked(message, 0);
		verify(mChatRepository).deleteMessages(message);
		verify(mView).remove(message, 0);
	}

	@Test
	public void testFiltersButtonClicked() throws Exception {

	}

	@Test
	public void testPictureTaken() throws Exception {
		File file = new File("");
		mPresenter.pictureTaken(file);

		ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
		verify(mView).showImage(captor.capture());
		assertEquals(file, captor.getValue());
	}

	@Test
	public void testOnCameraClick() throws Exception {
		mPresenter.onCameraClick();
		verify(mChatFragmentInteraction).onCameraClick();
	}

	@Test
	public void testClearImageButton() throws Exception {
		mPresenter.clearImageButton();
		verify(mView).showPictureContainer(false);
	}

	@Test
	public void testAttach() throws Exception {
		ChatContract.Presenter presenter = spy(mPresenter);
		presenter.attach();
		verify(presenter).loadMore(50, true);
	}
}