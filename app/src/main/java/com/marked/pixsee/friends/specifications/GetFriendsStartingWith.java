package com.marked.pixsee.friends.specifications;

import com.marked.pixsee.data.repository.SQLSpecification;
import com.marked.pixsee.friends.data.DatabaseFriendContract;

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
		return "SELECT * FROM " + DatabaseFriendContract.TABLE_NAME
				       +" WHERE " + DatabaseFriendContract.COLUMN_NAME
					   +" LIKE "
				       +'\'' + text +"%" +'\''
				       + " LIMIT " + limit + " OFFSET " + offset;
	}
}
