package com.marked.pixsee.ui.friends.mapper;

import android.database.Cursor;
import android.test.suitebuilder.annotation.SmallTest;

import com.marked.pixsee.data.user.User;
import com.marked.pixsee.ui.friends.data.FriendContractDB;

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
		User user = new User("123", "abc", "abc@gmail.com", "xyz123", null, "sweet");
		Mockito.when(mCursor.getString(1)).thenReturn(user.getId());
		Mockito.when(mCursor.getString(2)).thenReturn(user.getName());
		Mockito.when(mCursor.getString(4)).thenReturn(user.getEmail());
		Mockito.when(mCursor.getString(5)).thenReturn(user.getUserName());
		Mockito.when(mCursor.getString(7)).thenReturn(user.getPushToken());

		User user1 = mCursorToUserMapper.map(mCursor);
		Assert.assertEquals(user.getUserName(), user1.getUserName());
		Assert.assertEquals(user.getName(),user1.getName());
		Assert.assertEquals(user.getEmail(),user1.getEmail());
		Assert.assertEquals(user.getPushToken(), user1.getPushToken());
		Assert.assertEquals(user.getPassword(),user1.getPassword());
	}
}