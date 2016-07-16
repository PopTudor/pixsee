package com.marked.pixsee.friends.mapper;

import android.content.ContentValues;

import com.marked.pixsee.data.user.User;
import com.marked.pixsee.friends.data.FriendContractDB;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Tudor on 16-Jun-16.
 */
@RunWith(MockitoJUnitRunner.class)
public class CvToUserMapperTest {
	@Mock
	ContentValues mContentValues;
	CvToUserMapper mCvToUserMapper;
	User mUser;

	@Before
	public void setUp() throws Exception {
		mCvToUserMapper = new CvToUserMapper();
		mUser = new User("123", "abc", "abc@gmail.com", "abczxc", null, "http://somecover.com", "http://someicon.com", "abcD");
		Mockito.when(mContentValues.getAsString(FriendContractDB.COLUMN_ID)).thenReturn(mUser.getUserID());
		Mockito.when(mContentValues.getAsString(FriendContractDB.COLUMN_ICON_URL)).thenReturn(mUser.getIconUrl());
		Mockito.when(mContentValues.getAsString(FriendContractDB.COLUMN_COVER_URL)).thenReturn(mUser.getCoverUrl());
		Mockito.when(mContentValues.getAsString(FriendContractDB.COLUMN_USERNAME)).thenReturn(mUser.getUsername());
		Mockito.when(mContentValues.getAsString(FriendContractDB.COLUMN_EMAIL)).thenReturn(mUser.getEmail());
		Mockito.when(mContentValues.getAsString(FriendContractDB.COLUMN_TOKEN)).thenReturn(mUser.getToken());
		Mockito.when(mContentValues.getAsString(FriendContractDB.COLUMN_NAME)).thenReturn(mUser.getName());
	}

	@Test
	public void testMap() throws Exception {
		User user = mCvToUserMapper.map(mContentValues);
		Assert.assertEquals(user.getPassword(),mUser.getPassword());
		Assert.assertEquals(user.getUsername(),mUser.getUsername());
		Assert.assertEquals(user.getName(),mUser.getName());
		Assert.assertEquals(user.getEmail(),mUser.getEmail());
		Assert.assertEquals(user.getIconUrl(),mUser.getIconUrl());
		Assert.assertEquals(user.getCoverUrl(),mUser.getCoverUrl());
		Assert.assertEquals(user.getUserID(),mUser.getUserID());
	}
}