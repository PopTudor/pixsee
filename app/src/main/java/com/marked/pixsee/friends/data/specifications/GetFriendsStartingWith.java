package com.marked.pixsee.friends.data.specifications;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.repository.SQLSpecification;

/**
 * Created by Tudor on 2016-05-18.
 */
public class GetFriendsStartingWith implements SQLSpecification {
	private int offset;
	private int limit;
	private String text;

	public GetFriendsStartingWith(String text, int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
		this.text = text;
	}

	@Override
	public String createQuery() {
		return "SELECT * FROM " + DatabaseContract.Friend.TABLE_NAME
				       +" WHERE " + DatabaseContract.Friend.COLUMN_NAME
					   +" LIKE " + text +"%"
				       + " LIMIT " + limit + " OFFSET " + offset;
	}
}
