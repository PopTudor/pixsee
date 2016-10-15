package com.marked.pixsee.features.main;

import com.google.gson.JsonObject;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserRepository;
import com.marked.pixsee.features.main.strategy.ProfilePictureStrategy;
import com.marked.pixsee.features.main.strategy.ShareStrategy;

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

	User mUser = new User("", "", "", "");

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mMainPresenter = new MainPresenter(mView, mUserRepository);
	}

	@Test
	public void testFriendRequestOnlyUser() throws Exception {
		mMainPresenter.friendRequest(mUser);
		Mockito.verify(mView).showFriendRequestDialog(mUser);
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

		mMainPresenter.friendRequest(mUser, true);
		Mockito.verify(mUserRepository).saveUser(mUser);
	}

	@Test
	public void testFriendRequestRejected() throws Exception {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("test", true);
		Mockito.doReturn(Observable.just(jsonObject)).when(mUserRepository).saveUser(mUser);

		mMainPresenter.friendRequest(mUser, false);
	}


	@Test
	public void testChatTabClicked() throws Exception {
		mMainPresenter.chatTabClicked();
		Mockito.verify(mView).showChat(true);
	}

	@Test
	public void testProfileTabClicked() throws Exception {
		Mockito.doReturn(mUser).when(mUserRepository).getUser(Matchers.anyString());
		User user = mUserRepository.getUser(DatabaseContract.AppsUser.TABLE_NAME);

		mMainPresenter.profileTabClicked();
		Mockito.verify(mView).showProfile(user);
	}

	@Test
	public void testCameraTabClicked() throws Exception {
		mMainPresenter.cameraTabClicked();
		Mockito.verify(mView).hideBottomNavigation();
		Mockito.verify(mView).showCamera(Matchers.any(ShareStrategy.class));
	}

	@Test
	public void testProfileImageClicked() throws Exception {
		mMainPresenter.profileImageClicked();
		Mockito.verify(mView).hideBottomNavigation();
		Mockito.verify(mView).showCamera(Matchers.any(ProfilePictureStrategy.class));
	}

	@Test
	public void testAttach() throws Exception {
		MainContract.Presenter presenter = Mockito.spy(mMainPresenter);
		presenter.attach();
		Mockito.verify(presenter, Mockito.atLeastOnce()).chatTabClicked();
	}
}