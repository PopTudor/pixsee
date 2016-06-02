package com.marked.pixsee.friends.specifications;

import com.marked.pixsee.chat.data.MessageContract;
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

	public GetMessagesByGroupedByDate(String friendID) {
		this(-1, -1, friendID);
	}

	@Override
	public String createQuery() {
		if (offset < 0 || limit < 0)
			return "SELECT * FROM " + MessageContract.TABLE_NAME
					       + " WHERE " + MessageContract.COLUMN_ID + " = " + String.format("\'%s\'",friendID)
					       + " GROUP BY " + MessageContract.COLUMN_DATE;
		else
			return "SELECT * FROM " + MessageContract.TABLE_NAME
					       + " WHERE " + MessageContract.COLUMN_ID + " = " +  String.format("\'%s\'",friendID)
					       + " GROUP BY " + MessageContract.COLUMN_DATE
					       + " LIMIT " + limit
					       + " OFFSET " + offset;
	}
}
