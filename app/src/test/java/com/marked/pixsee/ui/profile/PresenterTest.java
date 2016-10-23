package com.marked.pixsee.ui.profile;

import com.marked.pixsee.data.user.UserRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Tudor on 15-Jun-16.
 */
public class PresenterTest {
	File[] mFiles = {new File("file1.txt"),
			new File("file2.txt")};
	ProfileContract.Presenter mPresenter;
	@Mock
	ProfileContract.View mView;
	@Mock
	UserRepository mUserDatasource;
	@Mock
	File mPublicPictureDirectoryFile ;

	@Captor
	ArgumentCaptor<List<String>> mListArgumentCaptor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(mPublicPictureDirectoryFile.listFiles()).thenReturn(mFiles);
		mPresenter = new Presenter(mView, mUserDatasource, mPublicPictureDirectoryFile);
	}

	@Test
	public void testSaveAppUser() throws Exception {
		User mUser = new User("", "", "", "");
		mPresenter.saveAppUser(mUser);
		verify(mUserDatasource).saveAppUser(mUser);
	}

	@Test
	public void testLogOut() throws Exception {
		mPresenter.logOut();
		verify(mUserDatasource).deleteAllUsers();
	}

	@Test
	public void testAttach() throws Exception {
		mPresenter.attach();
		verify(mView).setData(mListArgumentCaptor.capture());

		List<String> strings = new ArrayList<>();
		for (File it : mFiles)
			strings.add(it.getAbsolutePath());
		Assert.assertArrayEquals(strings.toArray(), mListArgumentCaptor.getValue().toArray());
	}

	@Test
	public void testInviteFriendsClicked() throws Exception {
		mPresenter.inviteFriendsClicked();
		verify(mView).showFriendsInvite();
	}
}