package com.marked.pixsee.profile;

import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.data.repository.user.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
/**
 * Created by Tudor on 15-Jun-16.
 */
public class PresenterTest {
	ProfileContract.Presenter mPresenter;
	@Mock
	ProfileContract.View mView;
	@Mock
	UserRepository mUserDatasource;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mPresenter = new Presenter(mView, mUserDatasource);
	}

	@Test
	public void testSaveAppUser() throws Exception {
		User mUser = new User("","","","");
		mPresenter.saveAppUser(mUser);
		Mockito.verify(mUserDatasource).saveAppUser(mUser);
	}

	@Test
	public void testStart() throws Exception {

	}

	@Test
	public void testLogOut() throws Exception {
		mPresenter.logOut();
		Mockito.verify(mUserDatasource).deleteAllUsers();
	}
}