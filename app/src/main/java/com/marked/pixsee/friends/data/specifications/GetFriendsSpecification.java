package com.marked.pixsee.friends.data.specifications;

import android.database.sqlite.SQLiteQueryBuilder;

import com.marked.pixsee.data.repository.SQLSpecification;
import com.marked.pixsee.data.database.DatabaseContract;


/**
 * Created by Tudor Pop on 29-Mar-16.
 */
public class GetFriendsSpecification implements SQLSpecification {
	private String[] cols = {DatabaseContract.Friend.COLUMN_ID, DatabaseContract.Friend.COLUMN_NAME, DatabaseContract.Friend.COLUMN_EMAIL,
	                         DatabaseContract.Friend.COLUMN_TOKEN};
	@Override
	public String createQuery() {
		return SQLiteQueryBuilder.buildQueryString(false, DatabaseContract.Friend.TABLE_NAME, cols, null, null, null, null, null);
	}
}
