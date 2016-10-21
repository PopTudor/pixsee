package com.marked.pixsee.ui.friends.specifications;

import com.marked.pixsee.ui.chat.data.MessageContract;

/**
 * Created by Tudor on 2016-05-19.
 */
public class GetMessagesByGroupedByDate {
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
