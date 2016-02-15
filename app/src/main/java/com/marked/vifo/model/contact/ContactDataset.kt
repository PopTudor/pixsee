package com.marked.vifo.model.contact

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.marked.vifo.extra.UserConstants
import com.marked.vifo.model.database.DatabaseContract
import com.marked.vifo.model.database.database
import org.jetbrains.anko.async
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
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
            update(DatabaseContract.Contact.TABLE_NAME, element.toContentValues(), "${DatabaseContract.Contact.COLUMN_ID} = ?s", arrayOf(element.id))
        }
        return super.set(index, element)
    }

    override fun remove(element: Contact): Boolean {
        mContext.database.use {
            delete(DatabaseContract.Contact.TABLE_NAME, "${DatabaseContract.Contact.COLUMN_ID} = ?s", arrayOf(element.id))
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

    fun contactListToJSONArray(list: List<Contact>): JSONArray {
        val jsonArray = JSONArray()
        for (contact in list)
            jsonArray.put(contact.toJSON())
        return jsonArray
    }

    fun loadMore(limit: Int=50) {
        mContext.database.use {
            select(DatabaseContract.Contact.TABLE_NAME,
                    DatabaseContract.Contact.COLUMN_ID,
                    DatabaseContract.Contact.COLUMN_NAME,
                    DatabaseContract.Contact.COLUMN_TOKEN).limit(size, limit).exec {
                parseList(rowParser {
                    id: String, name: String, token: String
                    ->
                    super.add(Contact(id, name, token))
                })
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
