package com.marked.pixsee.features.friends.specifications;

import com.marked.pixsee.features.friends.data.FriendContractDB;

/**
 * Created by Tudor on 2016-05-18.
 */
public class GetFriendsStartingWith {
	private int offset;
	private int limit;
	private String text;

	public GetFriendsStartingWith(String text, int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
		this.text = text;
	}

	public String createQuery() {
		return "SELECT * FROM " + FriendContractDB.TABLE_NAME
				       +" WHERE " + FriendContractDB.COLUMN_NAME
					   +" LIKE "
				       +'\'' + text +"%" +'\''
				       + " LIMIT " + limit + " OFFSET " + offset;
	}
}
