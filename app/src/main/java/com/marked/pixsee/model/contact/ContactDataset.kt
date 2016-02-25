package com.marked.pixsee.model.contact

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.marked.pixsee.extra.UserConstants
import com.marked.pixsee.model.database.DatabaseContract
import com.marked.pixsee.model.database.database
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
class ContactDataset(val mContext: Context) : ArrayList<Contact>() {
    init {
        loadMore()
    }

    override fun add(element: Contact): Boolean {
        mContext.database.use {
            insertWithOnConflict(DatabaseContract.Contact.TABLE_NAME, null, element.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
        }
        return super.add(element)
    }

    override fun addAll(elements: Collection<Contact>): Boolean {
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

    override fun set(index: Int, element: Contact): Contact {
        mContext.database.use {
            update(DatabaseContract.Contact.TABLE_NAME, element.toContentValues(), "${DatabaseContract.Contact.COLUMN_ID} = ?s", arrayOf(element.userID))
        }
        return super.set(index, element)
    }

    override fun remove(element: Contact): Boolean {
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
                    super.add(Contact(getString(id), getString(name), getString(email), getString(token)))
                    moveToNext()
                }
                close()
            }
        }
    }
}

fun JSONArray.contactListfromJSONArray(startingIndex: Int = 0): List<Contact> {
    val contacts = ArrayList<Contact>()

    var result: JSONObject
    var id: String
    var name: String
    var email: String
    var token: String
    for (i in startingIndex..length() - 1) {
        result = getJSONObject(i)
        id = result.getString(UserConstants.ID)
        name = result.getString(UserConstants.NAME)
        email = result.getString(UserConstants.EMAIL)
        token = result.getString(UserConstants.TOKEN)

        contacts.add(Contact(id, name, email, token))
    }

    return contacts
}
