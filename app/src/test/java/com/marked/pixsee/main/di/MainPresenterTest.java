package com.marked.pixsee.main.di;

import com.google.gson.JsonObject;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.data.repository.user.UserRepository;
import com.marked.pixsee.main.MainContract;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import rx.Observable;

/**
 * Created by Tudor on 17-Jun-16.
 */
public class MainPresenterTest {
	@Mock
	MainContract.View mView;
	@Mock
	UserRepository mUserRepository;
	MainPresenter mMainPresenter;
	@Captor
	ArgumentCaptor<User> mArgumentCaptor;

	User mUser = new User("","","","");

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mMainPresenter = new MainPresenter(mView, mUserRepository);
	}

	@Test
	public void testChatClicked() throws Exception {
		mMainPresenter.chatTabClicked();
		Mockito.verify(mView).showChat(true);
	}

	@Test
	public void testFriendRequestOnlyUser() throws Exception {
		mMainPresenter.friendRequest(mUser);
		Mockito.verify(mView).showFriendRequestDialog(mUser);
	}

	@Test
	public void testCameraClicked() throws Exception {
		mMainPresenter.cameraTabClicked();
		Mockito.verify(mView).hideBottomNavigation();
		Mockito.verify(mView).showCamera();
	}

	@Test
	public void testStart() throws Exception {
		mMainPresenter.attach();
		Mockito.verify(mView).showChat(true);
	}

	@Test
	public void testFriendRequestAccepted() throws Exception {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("test", true);
		Mockito.doReturn(Observable.just(jsonObject)).when(mUserRepository).saveUser(mUser);

		mMainPresenter.friendRequest(mUser,true);
		Mockito.verify(mUserRepository).saveUser(mUser);
	}

	@Test
	public void testFriendRequestRejected() throws Exception {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("test", true);
		Mockito.doReturn(Observable.just(jsonObject)).when(mUserRepository).saveUser(mUser);

		mMainPresenter.friendRequest(mUser,false);
	}

	@Test
	public void testProfileClicked() throws Exception {
		Mockito.doReturn(mUser).when(mUserRepository).getUser(Matchers.anyString());
		User user = mUserRepository.getUser(DatabaseContract.AppsUser.TABLE_NAME);

		mMainPresenter.profileTabClicked();
		Mockito.verify(mView).showProfile(user);
	}
}