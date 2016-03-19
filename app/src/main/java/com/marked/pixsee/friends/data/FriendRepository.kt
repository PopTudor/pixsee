package com.marked.pixsee.friends.data

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.marked.pixsee.data.Repository
import com.marked.pixsee.data.User
import com.marked.pixsee.data.database.DatabaseContract
import com.marked.pixsee.data.database.database
import org.jetbrains.anko.async
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.transaction
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


/**
 * Created by Tudor Pop on 12-Dec-15.
 * Singleton class used to keep all the friends of the user
 */
open class FriendRepository constructor(val mContext: Application) : ArrayList<User>(), Repository {
    init {
        loadMore(10)
    }

    override fun length(): Int {
        return size
    }

    override fun add(element: User): Boolean {
        mContext.database.use {
            insertWithOnConflict(DatabaseContract.Friend.TABLE_NAME, null, element.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
        }
        return super.add(element)
    }

    override fun addAll(elements: Collection<User>): Boolean {
        async() {
            mContext.database.use {
                transaction {
                    elements.forEach {
                        insertWithOnConflict(DatabaseContract.Friend.TABLE_NAME, null, it.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
                    }
                }
            }
        }
        return super.addAll(elements)
    }

    override fun set(index: Int, element: User): User {
        mContext.database.use {
            update(DatabaseContract.Friend.TABLE_NAME, element.toContentValues(), "${DatabaseContract.Friend.COLUMN_ID} = ?s", arrayOf(element.userID))
        }
        return super.set(index, element)
    }

    override fun remove(element: User): Boolean {
        mContext.database.use {
            delete(DatabaseContract.Friend.TABLE_NAME, "${DatabaseContract.Friend.COLUMN_ID} = ?s", arrayOf(element.userID))
        }
        return super.remove(element)
    }

    override
    fun loadMore(limit: Int) {
        mContext.database.use {
            select(DatabaseContract.Friend.TABLE_NAME,
                    DatabaseContract.Friend.COLUMN_ID,
                    DatabaseContract.Friend.COLUMN_NAME,
                    DatabaseContract.Friend.COLUMN_EMAIL,
                    DatabaseContract.Friend.COLUMN_TOKEN).limit(size, limit).exec {
                moveToFirst()
                val id = getColumnIndex(DatabaseContract.Friend.COLUMN_ID)
                val name = getColumnIndex(DatabaseContract.Friend.COLUMN_NAME)
                val email = getColumnIndex(DatabaseContract.Friend.COLUMN_EMAIL)
                val token = getColumnIndex(DatabaseContract.Friend.COLUMN_TOKEN)

                while (!isAfterLast) {
                    val friend = User(getString(id), getString(name), getString(email), getString(token))
                    super.add(friend)
                    moveToNext()
                }
                close()
            }
        }
    }
}

fun JSONArray.contactListfromJSONArray(startingIndex: Int = 0): List<User> {
    val contacts = ArrayList<User>()

    var result: JSONObject
    var id: String
    var name: String
    var email: String
    var token: String
    for (i in startingIndex..length() - 1) {
        result = getJSONObject(i)
        id = result.getString(FriendConstants.ID)
        name = result.getString(FriendConstants.NAME)
        email = result.getString(FriendConstants.EMAIL)
        token = result.getString(FriendConstants.TOKEN)

        contacts.add(User(id, name, email, token))
    }

    return contacts
}
