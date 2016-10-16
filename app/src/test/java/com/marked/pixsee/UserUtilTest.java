package com.marked.pixsee;

import com.marked.pixsee.data.user.User;

import org.junit.Assert;

/**
 * Created by Tudor on 23-Jul-16.
 */
public class UserUtilTest {
	public static final String USER_ID = "user_id_123";
	public static final String USER_NAME = "user_name";
	public static final String USER_USERNAME = "user_username";
	public static final String USER_EMAIL = "user_email";
	public static final String USER_COVERURL = "user_coverurl";
	public static final String USER_ICONURL = "user_iconurl";
	public static final String USER_TOKEN = "user_token";
	public static final String USER_PASSWORD = "user_password";

	public static User getUserTest() {
		return new User(USER_ID, USER_NAME, USER_EMAIL, USER_TOKEN, USER_PASSWORD, USER_COVERURL, USER_ICONURL, USER_USERNAME);
	}

	public static void assertUserProperties(User one, User two) {
		Assert.assertEquals(one.getUserID(), two.getUserID());
		Assert.assertEquals(one.getCoverUrl(), two.getCoverUrl());
		Assert.assertEquals(one.getEmail(), two.getEmail());
		Assert.assertEquals(one.getIconUrl(), two.getIconUrl());
		Assert.assertEquals(one.getName(), two.getName());
		Assert.assertEquals(one.getPassword(), two.getPassword());
		Assert.assertEquals(one.getToken(), two.getToken());
	}
}
