package com.marked.pixsee.entry;

import android.content.Intent;

import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.friends.data.FriendConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Tudor on 21-Jul-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class IntentToUserMapperTest {
	public static final String ID = "123";
	public static final String NAME = "name";
	public static final String EMAIL = "email";
	public static final String FROM = "from_token";
	public static final String ICON_URL = "icon";
	public static final String USERNAME = "username";
	public static final String COVER = "cover";

	Intent mIntent = new Intent();


	@Before
	public void setUp() throws Exception {
		mIntent.putExtra(FriendConstants.ID, ID);
		mIntent.putExtra(FriendConstants.NAME, NAME);
		mIntent.putExtra(FriendConstants.EMAIL, EMAIL);
		mIntent.putExtra(MessageConstants.FROM, FROM);
		mIntent.putExtra(FriendConstants.ICON_URL, ICON_URL);
		mIntent.putExtra(FriendConstants.USERNAME, USERNAME);
		mIntent.putExtra(FriendConstants.COVER_URL, COVER);
	}

	@Test
	public void testMap() throws Exception {
		IntentToUserMapper intentToUserMapper = new IntentToUserMapper();
		User user = intentToUserMapper.map(mIntent);
		Assert.assertEquals(user.getUserID(),ID);
		Assert.assertEquals(user.getName(),NAME);
		Assert.assertEquals(user.getEmail(),EMAIL);
		Assert.assertEquals(user.getToken(),FROM);
		Assert.assertEquals(user.getIconUrl(),ICON_URL);
		Assert.assertEquals(user.getUsername(),USERNAME);
		Assert.assertEquals(user.getPassword(),null);
		Assert.assertEquals(user.getCoverUrl(),COVER);
	}
}