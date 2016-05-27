package com.marked.pixsee.friends.specifications;

import com.marked.pixsee.data.repository.SQLSpecification;
import com.marked.pixsee.friends.data.DatabaseFriendContract;


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
			DatabaseFriendContract.COLUMN_ID,
			DatabaseFriendContract.COLUMN_NAME,
			DatabaseFriendContract.COLUMN_EMAIL,
			DatabaseFriendContract.COLUMN_TOKEN};

	@Override
	public String createQuery() {
		if (limit == -1)
			return "SELECT * FROM " + DatabaseFriendContract.TABLE_NAME;
		else
			return "SELECT * FROM " + DatabaseFriendContract.TABLE_NAME + " LIMIT " + limit + " OFFSET " + offset;
	}
}
