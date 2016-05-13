package com.marked.pixsee.friends.data.specifications;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.repository.SQLSpecification;


/**
 * Created by Tudor Pop on 29-Mar-16.
 */
public class GetFriendsSpecification implements SQLSpecification {
	private int offset;
	private int limit;

	public GetFriendsSpecification(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
	}

	private String[] cols = {
			DatabaseContract.Friend.COLUMN_ID,
			DatabaseContract.Friend.COLUMN_NAME,
			DatabaseContract.Friend.COLUMN_EMAIL,
			DatabaseContract.Friend.COLUMN_TOKEN};

	@Override
	public String createQuery() {
		return "SELECT * FROM " + DatabaseContract.Friend.TABLE_NAME + " LIMIT " + limit + " OFFSET " + offset;
	}
}
