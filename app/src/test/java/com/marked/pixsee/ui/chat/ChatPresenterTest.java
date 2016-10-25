package com.marked.pixsee.ui.chat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.marked.pixsee.TestSchedulerProxy;
import com.marked.pixsee.UserUtilTest;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.networking.UploadAPI;
import com.marked.pixsee.ui.chat.data.ChatRepository;
import com.marked.pixsee.utils.MessageTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Response;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Tudor on 18-Jun-16.
 */
@RunWith(RobolectricTestRunner.class)
public class ChatPresenterTest {
	private ChatPresenter mPresenter;
	private List<Message> mExpectedMessages = MessageTestUtils.getRandomMessageList(3);
	@Mock
	ChatContract.View mView;
	@Mock
	ChatRepository mChatRepository;
	@Mock
	ChatFragment.ChatFragmentInteraction mChatFragmentInteraction;
	@Mock
	UploadAPI uploadAPI;
	@Mock
	ChattingInterface mChattingInterface;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mPresenter = new ChatPresenter(mView, mChatRepository, UserUtilTest.getUserTest(), uploadAPI, mChattingInterface);
		mPresenter.setChatInteraction(mChatFragmentInteraction);
		mPresenter.setThatUser(UserUtilTest.getUserTest());
		TestSchedulerProxy.get();
	}

	@Test
	public void testLoadMoreByUserIdSuccess() throws Exception {
		when(mChatRepository.getMessages(any(User.class)))
				.thenReturn(Observable.just(mExpectedMessages));

		mPresenter.loadMore(50, true);

		verify(mChatRepository).getMessages(any(User.class));
		verify(mView).showMessages(mExpectedMessages);
	}

	@Test
	public void testLoadMoreByUserIdError() throws Exception {
		when(mChatRepository.getMessages(any(User.class)))
				.thenReturn(Observable.<List<Message>>error(new Error()));

		mPresenter.loadMore(50, true);

		verify(mChatRepository).getMessages(any(User.class));
		verify(mView).showEmptyMessages();
	}

	@Test
	public void testReceiveMessage() throws Exception {
		Message message = MessageTestUtils.getRandomMessage();
		mPresenter.receiveMessage(message);
		verify(mChatRepository).saveMessage(any(Message.class));
		verify(mView).addMessage(any(Message.class));
	}

	@Test
	public void testSendMessage() throws Exception {
		Mockito.doNothing().when(mChattingInterface).emit(anyString());
		mPresenter.sendMessage("message");
		verify(mChatRepository).saveMessage(any(Message.class));
		verify(mView).addMessage(any(Message.class));
	}

	@Test
	public void testSendMessageImage() throws Exception {
		JsonObject response = new JsonObject();
		response.add("statusCode",new JsonPrimitive(200));
		when(uploadAPI.upload(anyString(), anyString(), Matchers.<MultipartBody.Part>anyObject()))
				.thenReturn(Observable.just(Response.success(response)));
		mPresenter.sendMessage("test message text");
		verify(mChatRepository).saveMessage(any(Message.class));
		verify(mView).addMessage(any(Message.class));
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
	public void attachConnect_shouldEmitAppUserAndTargetUser() {
		when(mChatRepository.getMessages(any(User.class))).thenReturn(Observable.just(mExpectedMessages));

		ArgumentCaptor<JsonObject> jsonObjectArgumentCaptor = ArgumentCaptor.forClass(JsonObject.class);
		mPresenter.attach();
		verify(mChattingInterface).emit(anyString(), jsonObjectArgumentCaptor.capture());

		String from = jsonObjectArgumentCaptor.getValue().get("from").getAsString();
		String to = jsonObjectArgumentCaptor.getValue().get("to").getAsString();
		String toToken = jsonObjectArgumentCaptor.getValue().get("to_token").getAsString();
		JsonElement jsonElement = jsonObjectArgumentCaptor.getValue().get("user");
		JsonElement result = new JsonParser().parse(jsonElement.getAsString());
		User user = new Gson().fromJson(result, User.class);

		assertEquals(from, UserUtilTest.getUserTest().getUserID());
		assertEquals(to, UserUtilTest.getUserTest().getUserID());
		assertEquals(toToken, UserUtilTest.getUserTest().getToken());
		UserUtilTest.assertUserProperties(UserUtilTest.getUserTest(), user);
	}

}