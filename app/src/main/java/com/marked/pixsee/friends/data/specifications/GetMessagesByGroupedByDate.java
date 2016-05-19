package com.marked.pixsee.friends.data.specifications;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.repository.SQLSpecification;

/**
 * Created by Tudor on 2016-05-19.
 */
public class GetMessagesByGroupedByDate implements SQLSpecification {
	private int offset;
	private int limit;
	private String friendID;

	public GetMessagesByGroupedByDate(int offset, int limit, String friendID) {
		this.offset = offset;
		this.limit = limit;
		this.friendID = friendID;
	}

	@Override
	public String createQuery() {
		return "SELECT * FROM " + DatabaseContract.Message.TABLE_NAME
					   + " WHERE " + DatabaseContract.Message.COLUMN_ID + " = " + friendID
					   + " GROUP BY "+ DatabaseContract.Message.COLUMN_DATE
				       + " LIMIT " + limit
				       + " OFFSET " + offset;
	}
}
