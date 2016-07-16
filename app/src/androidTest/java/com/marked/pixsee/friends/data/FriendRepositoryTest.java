package com.marked.pixsee.friends.data;


import android.content.Context;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.user.UserRepository;
import com.marked.pixsee.data.user.UserDiskDatasource;
import com.marked.pixsee.data.user.UserNetworkDatasource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by Tudor Pop on 29-Mar-16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class FriendRepositoryTest {
	UserRepository friendRepository;
	Context context;
	PixyDatabase pixydb;

	@Before
	public void setupViewModel() {
		context = InstrumentationRegistry.getTargetContext();
		pixydb = PixyDatabase.getInstance(context);
		UserDiskDatasource friendsDiskDatasource = new UserDiskDatasource(pixydb);
		UserNetworkDatasource friendsNetworkDatasource = new UserNetworkDatasource(PreferenceManager.getDefaultSharedPreferences(context));
		friendRepository = new UserRepository(friendsDiskDatasource, friendsNetworkDatasource);
	}

	@Test
	public void testLength() {
//        Mockito.`when`(pixydb.readableDatabase.select(DatabaseContract.Friend.TABLE_NAME)).thenReturn(selectQueryBuilder)
//        Mockito.`when`(pixydb.readableDatabase.select(DatabaseContract.Friend.TABLE_NAME).exec { count }).thenReturn(0)
		Assert.assertEquals(0, friendRepository.length());
	}
}