package com.marked.pixsee.friends.data.specifications

import com.marked.pixsee.data.database.DatabaseContract
import com.marked.pixsee.data.database.DatabaseContract.Friend.Static.TABLE_NAME
import com.marked.pixsee.data.repository.SQLSpecification


/**
 * Created by Tudor Pop on 29-Mar-16.
 */
class GetFriendsSpecification(internal var offset: Int,
                              internal var limit: Int) : SQLSpecification {
    private val cols = arrayOf(DatabaseContract.Friend.COLUMN_ID, DatabaseContract.Friend.COLUMN_NAME, DatabaseContract.Friend.COLUMN_EMAIL, DatabaseContract.Friend.COLUMN_TOKEN)

    override fun createQuery(): String {
        return "SELECT * FROM ${TABLE_NAME} LIMIT $limit OFFSET $offset"
    }
}
