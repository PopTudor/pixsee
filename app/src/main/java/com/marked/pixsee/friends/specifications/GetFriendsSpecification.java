package com.marked.pixsee.friends.specifications;

import com.marked.pixsee.data.repository.SQLSpecification;
import com.marked.pixsee.friends.data.FriendContractDB;


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
			FriendContractDB.COLUMN_ID,
			FriendContractDB.COLUMN_NAME,
			FriendContractDB.COLUMN_EMAIL,
			FriendContractDB.COLUMN_TOKEN};

	@Override
	public String createQuery() {
		if (limit == -1)
			return "SELECT * FROM " + FriendContractDB.TABLE_NAME;
		else
			return "SELECT * FROM " + FriendContractDB.TABLE_NAME + " LIMIT " + limit + " OFFSET " + offset;
	}
}
