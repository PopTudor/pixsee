package com.marked.pixsee.ui.friends.specifications;

import com.marked.pixsee.data.friends.FriendContractDB;


/**
 * Created by Tudor Pop on 29-Mar-16.
 */
public class GetFriendsSpecification {
	private int offset;
	private int limit;
	private String[] cols = {
			FriendContractDB.COLUMN_ID,
			FriendContractDB.COLUMN_NAME,
			FriendContractDB.COLUMN_EMAIL,
			FriendContractDB.COLUMN_TOKEN};

	public GetFriendsSpecification(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
	}

	public String createQuery() {
		if (limit == -1)
			return "SELECT * FROM " + FriendContractDB.TABLE_NAME;
		else
			return "SELECT * FROM " + FriendContractDB.TABLE_NAME + " LIMIT " + limit + " OFFSET " + offset;
	}
}
