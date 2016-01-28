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
	//todo create a cache for stored elements inside the database for performance
	fun addContact(contact: Contact) = mContext.database.use {
		insertWithOnConflict(DatabaseContract.Contact.TABLE_NAME, null, contact.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
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
		val res = mContext.database.use {
			select(DatabaseContract.Contact.TABLE_NAME).exec {
				count
			}
		}
		return res
	}

	fun updateContact(contact: Contact) = mContext.database.use {
		update(DatabaseContract.Contact.TABLE_NAME, contact.toContentValues(), "${DatabaseContract.Contact.COLUMN_ID} = ?s", arrayOf(contact.id))
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
		delete(DatabaseContract.Contact.TABLE_NAME, "${DatabaseContract.Contact.COLUMN_ID} = ?s", arrayOf(contact.id))
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

	fun getContact(id: String): Contact {
		val result = mContext.database.use {
			select(DatabaseContract.Contact.TABLE_NAME).where("${DatabaseContract.Contact.COLUMN_ID}={id}", "id" to id).exec {
				parseSingle(rowParser {
					id: String, fname: String, lname: String, token: String ->
					Contact(id, fname, lname, token = token)
				})
			}
		}
		return result
	}

	fun getContact(id: List<String>): List<Contact> {
		val contacts = arrayListOf<Contact>()
		mContext.database.use {
			select(DatabaseContract.Contact.TABLE_NAME).where("${DatabaseContract.Contact.COLUMN_ID} IN ({id})", "id" to id).exec {
				parseList(rowParser {
					id: String, fname: String, lname: String, token: String ->
					contacts.add(Contact(id, fname, lname, token = token))
				})
			}
		}
		return contacts
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
		return contacts
	}


	companion object {
		val contacts: Contacts? = null
		fun getInstance(context: Context): Contacts {
			if (contacts == null)
				return Contacts(context)
			return contacts
		}

	}


	fun fromJSONArray(jsonArray: JSONArray, startingIndex: Int = 0): List<Contact> {
		val contacts = ArrayList<Contact>()

		var result: JSONObject
		var id: String
		var firstName: String
		var lastName: String
		var token: String
		for (i in startingIndex..jsonArray.length() - 1) {
			result = jsonArray.getJSONObject(i)
			id = result.getString(UserConstants.ID)
			firstName = result.getString(UserConstants.FIRST_NAME)
			lastName = result.getString(UserConstants.LAST_NAME)
			token = result.getString(UserConstants.TOKEN)

			contacts.add(Contact(id, firstName, lastName, firstName + " " + lastName, token))
		}

		return contacts
	}

	fun toJSONArray(list: List<Contact>): JSONArray {
		val jsonArray = JSONArray()
		for (contact in list)
			jsonArray.put(contact.toJSON())
		return jsonArray
	}

}
