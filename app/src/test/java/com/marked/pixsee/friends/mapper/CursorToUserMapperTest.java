package com.marked.pixsee.friends.mapper;

import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.friends.data.FriendContractDB;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Created by Tudor on 16-Jun-16.
 */
@SmallTest
public class CursorToUserMapperTest {
	CursorToUserMapper mCursorToUserMapper;
	@Mock
	Cursor mCursor;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mCursorToUserMapper = new CursorToUserMapper();
		Mockito.when(mCursor.getColumnIndex(FriendContractDB.COLUMN_ID)).thenReturn(1);
		Mockito.when(mCursor.getColumnIndex(FriendContractDB.COLUMN_NAME)).thenReturn(2);
		Mockito.when(mCursor.getColumnIndex(FriendContractDB.COLUMN_COVER_URL)).thenReturn(3);
		Mockito.when(mCursor.getColumnIndex(FriendContractDB.COLUMN_EMAIL)).thenReturn(4);
		Mockito.when(mCursor.getColumnIndex(FriendContractDB.COLUMN_USERNAME)).thenReturn(5);
		Mockito.when(mCursor.getColumnIndex(FriendContractDB.COLUMN_ICON_URL)).thenReturn(6);
		Mockito.when(mCursor.getColumnIndex(FriendContractDB.COLUMN_TOKEN)).thenReturn(7);

	}

	@Test
	public void testMap() throws Exception {
		User user = new User("123", "abc", "abc@gmail.com", "xyz123", null, "https://someCover", "https://someIcon", "sweet");
		Mockito.when(mCursor.getString(1)).thenReturn(user.getUserID());
		Mockito.when(mCursor.getString(2)).thenReturn(user.getName());
		Mockito.when(mCursor.getString(3)).thenReturn(user.getCoverUrl());
		Mockito.when(mCursor.getString(4)).thenReturn(user.getEmail());
		Mockito.when(mCursor.getString(5)).thenReturn(user.getUsername());
		Mockito.when(mCursor.getString(6)).thenReturn(user.getIconUrl());
		Mockito.when(mCursor.getString(7)).thenReturn(user.getToken());

		User user1 = mCursorToUserMapper.map(mCursor);
		Assert.assertEquals(user.getUsername(),user1.getUsername());
		Assert.assertEquals(user.getCoverUrl(),user1.getCoverUrl());
		Assert.assertEquals(user.getIconUrl(),user1.getIconUrl());
		Assert.assertEquals(user.getName(),user1.getName());
		Assert.assertEquals(user.getEmail(),user1.getEmail());
		Assert.assertEquals(user.getToken(),user1.getToken());
		Assert.assertEquals(user.getPassword(),user1.getPassword());
	}
}