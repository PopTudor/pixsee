package com.marked.vifo.model.message

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.marked.vifo.extra.MessageConstants
import com.marked.vifo.model.contact.Contact
import com.marked.vifo.model.database.DatabaseContract
import com.marked.vifo.model.database.database
import org.jetbrains.anko.async
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.transaction
import org.json.JSONArray
import java.util.*

/**
 * Created by Tudor Pop on 14-Feb-16.
 */
class MessageDataset(var mContext: Context) : ArrayList<Message>() {

	override fun add(element: Message): Boolean {
		if(element.messageType != MessageConstants.MessageType.TYPING) /* if the message is a 'typing' message, then don't add it to the database */
			mContext.database.use {
				insertWithOnConflict(DatabaseContract.Message.TABLE_NAME, null, element.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
			}
		return super.add(element)
	}

	override fun addAll(elements: Collection<Message>): Boolean {
		var result = super.addAll(elements)
		async() {
			mContext.database.use {
				transaction {
					elements.forEach {
						insertWithOnConflict(DatabaseContract.Message.TABLE_NAME, null, it.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE)
					}
				}
			}
		}
		return result
	}

	override fun set(index: Int, element: Message): Message {
		mContext.database.use {
			update(DatabaseContract.Message.TABLE_NAME, element.toContentValues(), "${DatabaseContract.Message.COLUMN_ID} = ?s", arrayOf(element.id))
		}
		return super.set(index, element)
	}

	override fun remove(element: Message): Boolean {
		mContext.database.use {
			delete(DatabaseContract.Message.TABLE_NAME, "${DatabaseContract.Message.COLUMN_ID} = ?s", arrayOf(element.id))
		}
		return super.remove(element)
	}

	companion object {
		val messages: MessageDataset? = null
		fun getInstance(context: Context): MessageDataset {
			if (messages == null)
				return MessageDataset(context)
			return messages
		}
	}

	fun MessageListToJSONArray(list: List<Message>): JSONArray {
		val jsonArray = JSONArray()
		for (Message in list)
			jsonArray.put(Message.toJSON())
		return jsonArray
	}

	fun loadMore(contact: Contact, limit: Int = 50) {
		mContext.database.use {
			select(DatabaseContract.Message.TABLE_NAME,
					DatabaseContract.Message.COLUMN_DATA_BODY,
					DatabaseContract.Message.COLUMN_TYPE,
					DatabaseContract.Message.COLUMN_DATE,
					DatabaseContract.Message.COLUMN_TO
			).where("${DatabaseContract.Message.COLUMN_TO}={to}", "to" to contact.id).limit(size, limit)
					.exec {
						parseList(rowParser {
							body: String, type: Int, date: Int, to: String
							->
							val message = Message.Builder().addData(MessageConstants.DATA_BODY, body).messageType(type).date(date.toLong()).to(to).build()
							super.add(message)
						})
						//			}
					}
		}
	}
}