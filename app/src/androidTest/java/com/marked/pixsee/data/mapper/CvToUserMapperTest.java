package com.marked.pixsee.data.mapper;

import android.content.ContentValues;
import android.support.test.runner.AndroidJUnit4;

import com.marked.pixsee.friends.friends.data.User;
import com.marked.pixsee.data.database.DatabaseContract;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by Tudor Pop on 01-Apr-16.
 */
@RunWith(AndroidJUnit4.class)
public class CvToUserMapperTest {
	private CvToUserMapper mUserToCvMapper = new CvToUserMapper();
	private ContentValues  mContentValues  = new ContentValues();

	@Test
	public void testMap() throws Exception {
		String id = "123";
		String name = "Duyald Dawnflame";
		String email = "dawnflame@gmai.com";
		String token = "321321";

		mContentValues.put(DatabaseContract.Friend.COLUMN_ID, id);
		mContentValues.put(DatabaseContract.Friend.COLUMN_NAME, name);
		mContentValues.put(DatabaseContract.Friend.COLUMN_EMAIL, email);
		mContentValues.put(DatabaseContract.Friend.COLUMN_TOKEN, token);

		User user = mUserToCvMapper.map(mContentValues);

		assertThat(user.getUserID(), equalTo(id));
		assertThat(user.getName(), equalTo(name));
		assertThat(user.getEmail(), equalTo(email));
		assertThat(user.getToken(), equalTo(token));
	}
}