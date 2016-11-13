package com.marked.pixsee.ui.friends.mapper;

import android.content.ContentValues;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.marked.pixsee.data.friends.FriendConstants;
import com.marked.pixsee.data.user.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Tudor on 6/2/2016.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class UserToCvMapperTest {
	UserToCvMapper userToCvMapper = new UserToCvMapper();

	@Before
	public void setUp(){
	}
	@Test
	public void testMap() throws Exception {
		User user = new User("id_123", "name_user1", "user1@email.com",
				                    "someToken", "password", "user1");
		ContentValues result = userToCvMapper.map(user);

		Assert.assertEquals(user.getId(), result.getAsString(FriendConstants.ID));
		Assert.assertEquals(user.getName(),result.getAsString(FriendConstants.NAME));
		Assert.assertEquals(user.getEmail(),result.getAsString(FriendConstants.EMAIL));
		Assert.assertEquals(user.getPushToken(), result.getAsString(FriendConstants.TOKEN));
		Assert.assertEquals(user.getPassword(),result.getAsString(FriendConstants.PASSWORD));
		Assert.assertEquals(user.getUsername(), result.getAsString(FriendConstants.USERNAME));
	}
}