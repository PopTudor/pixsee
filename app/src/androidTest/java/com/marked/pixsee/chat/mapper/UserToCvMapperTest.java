package com.marked.pixsee.chat.mapper;

import android.content.ContentValues;
import android.support.test.runner.AndroidJUnit4;

import com.marked.pixsee.friends.data.FriendContractDB;
import com.marked.pixsee.friends.mapper.UserToCvMapper;
import com.marked.pixsee.model.user.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by Tudor Pop on 01-Apr-16.
 */
@RunWith(AndroidJUnit4.class)
public class UserToCvMapperTest {
	private UserToCvMapper mUserToCvMapper = new UserToCvMapper();
	private User mUser           = new User("123", "Duyald Dawnflame", "dawnflame@gmai.com", "321321");

	@Test
	public void testMap() throws Exception {
		ContentValues contentValues = mUserToCvMapper.map(mUser);
		String id = contentValues.getAsString(FriendContractDB.COLUMN_ID);
		String name = contentValues.getAsString(FriendContractDB.COLUMN_NAME);
		String email = contentValues.getAsString(FriendContractDB.COLUMN_EMAIL);
		String token = contentValues.getAsString(FriendContractDB.COLUMN_TOKEN);

		assertThat(mUser.getUserID(), equalTo(id));
		assertThat(mUser.getName(), equalTo(name));
		assertThat(mUser.getEmail(), equalTo(email));
		assertThat(mUser.getToken(), equalTo(token));
	}
}