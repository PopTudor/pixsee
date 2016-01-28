package com.marked.vifo.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.marked.vifo.database.DatabaseContract
import com.marked.vifo.database.database
import com.marked.vifo.extra.UserConstants
import org.jetbrains.anko.async
import org.jetbrains.anko.db.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * Created by Tudor Pop on 12-Dec-15.
 * Singleton class used to keep all the friends (the list of contacts) of the user
 */
class Contacts(val mContext: android.content.Context) {
	val mContacts: MutableList<Contact> = arrayListOf()

	//todo create a cache for stored elements inside the database for performance
	fun addContact(contact: Contact) = mContext.database.use {
		if (!mContacts.contains(contact)) {
			mContacts.add(contact)
			insertWithOnConflict(DatabaseContract.Contact.TABLE_NAME, null, contact.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
		}
	}

	fun addContact(contacts: List<Contact>) = async() {
		mContext.database.use {
			transaction {
				contacts.forEach {
					addContact(it)
				}
			}
		}
	}

	fun getCount(): Int {
		val itemsCounted = mContext.database.use {
			select(DatabaseContract.Contact.TABLE_NAME).exec {
				count
			}
		}
		return itemsCounted
	}

	fun updateContact(contact: Contact) = mContext.database.use {
		val index = mContacts.indexOf(contact)
		if (index > -1) {
			update(DatabaseContract.Contact.TABLE_NAME, contact.toContentValues(), "${DatabaseContract.Contact.COLUMN_ID} = ?s", arrayOf(contact.id))
			mContacts[index] = contact
		}
	}

	fun updateContact(contacts: List<Contact>) = async() {
		mContext.database.use {
			transaction {
				contacts.forEach {
					updateContact(it)
				}
			}
		}
	}

	fun deleteContact(contact: Contact) = mContext.database.use {
		val index = mContacts.indexOf(contact)
		if (index > -1) {
			delete(DatabaseContract.Contact.TABLE_NAME, "${DatabaseContract.Contact.COLUMN_ID} = ?s", arrayOf(contact.id))
			return@use mContacts.remove(contact)
		}
		return@use false
	}

	fun deleteContact(contacts: List<Contact>) = async() {
		mContext.database.use {
			transaction {
				contacts.forEach {
					deleteContact(it)
				}
			}
		}
	}

	fun getContact(contact: Contact): Contact {
		if (mContacts.contains(contact))
			return mContacts.get(mContacts.indexOf(contact))
		else {
			val result = mContext.database.use {
				select(DatabaseContract.Contact.TABLE_NAME).where("${DatabaseContract.Contact.COLUMN_ID}={id}", "id" to contact.id).exec {
					parseSingle(rowParser {
						id: String, fname: String, lname: String, token: String ->
						Contact(id, fname, lname, token = token)
					})
				}
			}
			return result
		}
	}

	fun getContact(id: List<String>): MutableList<Contact> {
		val contacts = arrayListOf<Contact>()
		mContext.database.use {
			select(DatabaseContract.Contact.TABLE_NAME).where("${DatabaseContract.Contact.COLUMN_ID} IN ({id})", "id" to id).exec {
				parseList(rowParser {
					id: String, fname: String, lname: String, token: String ->
					contacts.add(Contact(id, fname, lname, token = token))
				})
			}
		}
		if (mContacts.size != contacts.size) {
			mContacts.clear()
			mContacts.addAll(contacts)
		}
		return mContacts
	}

	fun getContact(): MutableList<Contact> {
		val contacts = arrayListOf<Contact>()
		mContext.database.use {
			select(DatabaseContract.Contact.TABLE_NAME).exec {
				parseList(rowParser {
					id: String, fname: String, lname: String, token: String ->
					contacts.add(Contact(id, fname, lname, token = token))
				})
			}
		}
		if (mContacts.size != contacts.size) {
			mContacts.clear()
			mContacts.addAll(contacts)
		}
		return mContacts
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
}

fun JSONArray.contactListfromJSONArray(startingIndex: Int = 0): List<Contact> {
	val contacts = ArrayList<Contact>()

	var result: JSONObject
	var id: String
	var firstName: String
	var lastName: String
	var token: String
	for (i in startingIndex..length() - 1) {
		result = getJSONObject(i)
		id = result.getString(UserConstants.ID)
		firstName = result.getString(UserConstants.FIRST_NAME)
		lastName = result.getString(UserConstants.LAST_NAME)
		token = result.getString(UserConstants.TOKEN)

		contacts.add(Contact(id, firstName, lastName, firstName + " " + lastName, token))
	}

	return contacts
}
