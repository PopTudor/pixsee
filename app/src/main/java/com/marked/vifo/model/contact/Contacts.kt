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
class Contacts(val mContext: Context) : ArrayList<Contact>() {
	init {
		loadMore(50)
	}

	override fun add(element: Contact): Boolean {
		mContext.database.use {
			insertWithOnConflict(DatabaseContract.Contact.TABLE_NAME, null, element.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
		}
		return super.add(element)
	}

	override fun addAll(elements: Collection<Contact>): Boolean {
		async() {
			mContext.database.use {
				transaction {
					elements.forEach {
						insertWithOnConflict(DatabaseContract.Contact.TABLE_NAME, null, it.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
					}
				}
			}
		}
		return super.addAll(elements)
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
		val contacts: Contacts? = null
		fun getInstance(context: Context): Contacts {
			if (contacts == null)
				return Contacts(context)
			return contacts
		}
	}

	fun contactListToJSONArray(list: List<Contact>): JSONArray {
		val jsonArray = JSONArray()
		for (contact in list)
			jsonArray.put(contact.toJSON())
		return jsonArray
	}

	fun loadMore(limit:Int) {
		mContext.database.use {
			select(DatabaseContract.Contact.TABLE_NAME).limit(size, limit).exec {
				parseList(rowParser {
					id: String, name:String, token: String
					->
					add(Contact(id, name,token))
				})
			}
		}
	}
}

fun JSONArray.contactListfromJSONArray(startingIndex: Int = 0): List<Contact> {
	val contacts = ArrayList<Contact>()

	var result: JSONObject
	var id: String
	var token: String
	var name: String
	for (i in startingIndex..length() - 1) {
		result = getJSONObject(i)
		id = result.getString(UserConstants.ID)
		name = result.getString(UserConstants.NAME)
		token = result.getString(UserConstants.TOKEN)

		contacts.add(Contact(id, name, token))
	}

	return contacts
}
