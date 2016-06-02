package com.marked.pixsee.friends.mapper;

import android.content.ContentValues;

import com.marked.pixsee.friends.data.FriendConstants;
import com.marked.pixsee.friends.data.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Tudor on 6/2/2016.
 */
public class UserToCvMapperTest {
	UserToCvMapper userToCvMapper = new UserToCvMapper();

	@Before
	void setUp(){
		ContentValues contentValues = new ContentValues();
	}
	@Test
	public void testMap() throws Exception {
		User user = new User("id_123", "name_user1", "user1@email.com",
				"someToken", "password", "http://someurl.com", "http://someicon.com", "user1");
		ContentValues result = userToCvMapper.map(user);

		Assert.assertEquals(user.getUserID(),result.getAsString(FriendConstants.ID));
		Assert.assertEquals(user.getName(),result.getAsString(FriendConstants.NAME));
		Assert.assertEquals(user.getEmail(),result.getAsString(FriendConstants.EMAIL));
		Assert.assertEquals(user.getToken(),result.getAsString(FriendConstants.TOKEN));
		Assert.assertEquals(user.getPassword(),result.getAsString(FriendConstants.PASSWORD));
		Assert.assertEquals(user.getCoverUrl(),result.getAsString(FriendConstants.COVER_URL));
		Assert.assertEquals(user.getIconUrl(),result.getAsString(FriendConstants.ICON_URL));
		Assert.assertEquals(user.getUsername(),result.getAsString(FriendConstants.USERNAME));
	}
}