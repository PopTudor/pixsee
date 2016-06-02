package com.marked.pixsee.data.mapper;

import android.test.mock.MockCursor;

import com.google.common.collect.Lists;
import com.marked.pixsee.friends.data.FriendContractDB;
import com.marked.pixsee.friends.data.User;
import com.marked.pixsee.friends.mapper.CursorToUserMapper;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by Tudor Pop on 01-Apr-16.
 */
public class CursorToUserMapperTest {
	private static List<User> sFRIENDS = Lists.newArrayList(new User("user1", "xyz", "xyz@emai.com", "asd"),
	                                                        new User("user2", "zxc", "zxc@emai.com", "zxc"),
	                                                        new User("user3", "zxc", "zxc@emai.com", "zxc"),
	                                                        new User("user4", "zxc", "zxc@emai.com", "zxc"));

	private CursorToUserMapper mCursorToUserMapper = new CursorToUserMapper();

	private MockCursor mCursor = new MockCursor() {
		int mCurrentPosition = -1;

		@Override
		public int getCount() {
			return sFRIENDS.size();
		}

		@Override
		public boolean moveToFirst() {
			mCurrentPosition = 0;
			return true;
		}

		@Override
		public boolean moveToNext() {
			if (mCurrentPosition < sFRIENDS.size()) {
				mCurrentPosition++;
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean isBeforeFirst() {
			return mCurrentPosition < 0;
		}

		@Override
		public boolean isAfterLast() {
			return mCurrentPosition >= sFRIENDS.size();
		}

		@Override
		public int getColumnIndex(String columnName) {
			if (columnName.equals(FriendContractDB.COLUMN_ID))
				return 0;
			else if ((columnName.equals(FriendContractDB.COLUMN_NAME)))
				return 1;
			else if (columnName.equals(FriendContractDB.COLUMN_EMAIL))
				return 2;
			else if (columnName.equals(FriendContractDB.COLUMN_TOKEN))
				return 3;
			else
				return -1;
		}

		@Override
		public String getString(int columnIndex) {
			org.junit.Assert.assertTrue(mCurrentPosition >= 0 && mCurrentPosition < sFRIENDS.size());
			User user = sFRIENDS.get(mCurrentPosition);
			switch (columnIndex) {
				case 0:
					return user.getUserID();
				case 1:
					return user.getName();
				case 2:
					return user.getEmail();
				case 3:
					return user.getToken();
				default:
					return user.getUserID();
			}
		}

		@Override
		public void close() {
		}
	};


	@Test
	public void testMap() throws Exception {
		mCursor.moveToFirst();
		int i = 0;
		while (!mCursor.isAfterLast()) {
			User user = mCursorToUserMapper.map(mCursor);
			Assert.assertThat(user, equalTo(sFRIENDS.get(i++)));
			mCursor.moveToNext();
		}
	}
}