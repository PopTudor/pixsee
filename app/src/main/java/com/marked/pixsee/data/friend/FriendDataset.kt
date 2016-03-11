package com.marked.pixsee.data.friend

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.marked.pixsee.data.friend.FriendConstants
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
 * Singleton class used to keep all the friends (the list of contacts) of the user
 */
class ContactDataset(val mContext: Context) : ArrayList<Friend>() {
    init {
        loadMore()
    }

    override fun add(element: Friend): Boolean {
        mContext.database.use {
            insertWithOnConflict(DatabaseContract.Contact.TABLE_NAME, null, element.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
        }
        return super.add(element)
    }

    override fun addAll(elements: Collection<Friend>): Boolean {
        var result = super.addAll(elements)
        async() {
            mContext.database.use {
                transaction {
                    elements.forEach {
                        insertWithOnConflict(DatabaseContract.Contact.TABLE_NAME, null, it.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
                    }
                }
            }
        }
        return result
    }

    override fun set(index: Int, element: Friend): Friend {
        mContext.database.use {
            update(DatabaseContract.Contact.TABLE_NAME, element.toContentValues(), "${DatabaseContract.Contact.COLUMN_ID} = ?s", arrayOf(element.userID))
        }
        return super.set(index, element)
    }

    override fun remove(element: Friend): Boolean {
        mContext.database.use {
            delete(DatabaseContract.Contact.TABLE_NAME, "${DatabaseContract.Contact.COLUMN_ID} = ?s", arrayOf(element.userID))
        }
        return super.remove(element)
    }

    companion object {
        val contacts: ContactDataset? = null
        fun getInstance(context: Context): ContactDataset {
            if (contacts == null)
                return ContactDataset(context)
            return contacts
        }
    }

    fun loadMore(limit: Int = 50) {
        mContext.database.use {

            select(DatabaseContract.Contact.TABLE_NAME,
                    DatabaseContract.Contact.COLUMN_ID,
                    DatabaseContract.Contact.COLUMN_NAME,
                    DatabaseContract.Contact.COLUMN_EMAIL,
                    DatabaseContract.Contact.COLUMN_TOKEN).limit(size, limit).exec {
                moveToFirst()
                val id = getColumnIndex(DatabaseContract.Contact.COLUMN_ID)
                val name = getColumnIndex(DatabaseContract.Contact.COLUMN_NAME)
                val email = getColumnIndex(DatabaseContract.Contact.COLUMN_EMAIL)
                val token = getColumnIndex(DatabaseContract.Contact.COLUMN_TOKEN)

                while (!isAfterLast) {
                    super.add(Friend(getString(id), getString(name), getString(email), getString(token)))
                    moveToNext()
                }
                close()
            }
        }
    }
}

fun JSONArray.contactListfromJSONArray(startingIndex: Int = 0): List<Friend> {
    val contacts = ArrayList<Friend>()

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

        contacts.add(Friend(id, name, email, token))
    }

    return contacts
}
