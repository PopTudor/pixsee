package com.marked.pixsee.ui.friends.mapper;

import android.content.ContentValues;

import com.marked.pixsee.data.friends.FriendContractDB;
import com.marked.pixsee.data.user.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by Tudor on 16-Jun-16.
 */
@RunWith(RobolectricTestRunner.class)
public class CvToUserMapperTest {
	private ContentValues mContentValues;
	private CvToUserMapper mCvToUserMapper;
	private User mUser;


	@Before
	public void setUp() throws Exception {
		mContentValues = new ContentValues();
		mCvToUserMapper = new CvToUserMapper();
		mUser = new User("123", "abc", "abc@gmail.com", "abczxc", null, "abcD");
		mContentValues.put(FriendContractDB.COLUMN_ID, mUser.getId());
		mContentValues.put(FriendContractDB.COLUMN_USERNAME, mUser.getUserName());
		mContentValues.put(FriendContractDB.COLUMN_EMAIL, mUser.getEmail());
		mContentValues.put(FriendContractDB.COLUMN_TOKEN, mUser.getPushToken());
		mContentValues.put(FriendContractDB.COLUMN_NAME, mUser.getName());
	}

	@Test
	public void testMap() throws Exception {
		User user = mCvToUserMapper.map(mContentValues);
		Assert.assertEquals(user.getPassword(), mUser.getPassword());
		Assert.assertEquals(user.getUserName(), mUser.getUserName());
		Assert.assertEquals(user.getName(), mUser.getName());
		Assert.assertEquals(user.getEmail(), mUser.getEmail());
		Assert.assertEquals(user.getId(), mUser.getId());
	}
}