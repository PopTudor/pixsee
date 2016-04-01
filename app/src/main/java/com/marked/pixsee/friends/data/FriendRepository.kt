package com.marked.pixsee.friends.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.marked.pixsee.data.User
import com.marked.pixsee.data.database.DatabaseContract
import com.marked.pixsee.data.database.DatabaseContract.Friend.Static.TABLE_NAME
import com.marked.pixsee.data.database.PixyDatabase
import com.marked.pixsee.data.mapper.CursorToUserMapper
import com.marked.pixsee.data.mapper.Mapper
import com.marked.pixsee.data.mapper.UserToCvMapper
import com.marked.pixsee.data.repository.Repository
import com.marked.pixsee.data.repository.SQLSpecification
import com.marked.pixsee.data.repository.Specification
import com.marked.pixsee.utility.apply
import org.jetbrains.anko.async
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.transaction
import org.json.JSONArray
import org.json.JSONObject
import rx.Observable
import java.util.*


/**
 * Created by Tudor Pop on 12-Dec-15.
 * Singleton class used to keep all the friends of the user
 */
class FriendRepository constructor(val db: PixyDatabase) : Repository<User> {
    private val cache: MutableList<User> by lazy { mutableListOf<User>() }
    private val cursorToUserMapper: Mapper<Cursor, User> by lazy { CursorToUserMapper() }
    private val UserToCvMapper: Mapper<User, ContentValues> by lazy { UserToCvMapper() }

    fun length(): Int {
        var size = cache.size
        if (size == 0)
            db.readableDatabase.select(TABLE_NAME).exec { size = count }
        return size;
    }

    override fun update(item: User) {
        db.use {
            update(TABLE_NAME, UserToCvMapper.map(item), "${DatabaseContract.Friend.COLUMN_ID} = ?s", arrayOf(item.userID))
        }
        cache.set(cache.indexOf(item), item)
    }

    override fun remove(specification: Specification?) {
        throw UnsupportedOperationException()
    }

    override fun query(specification: Specification?): Observable<MutableList<User>>? {
        db.use {
            if (specification is SQLSpecification) {
                rawQuery(specification.createQuery(), null).apply {
                    while (!isAfterLast) {
                        val friend = cursorToUserMapper.map(this)
                        cache.add(friend)
                        moveToNext()
                    }
                }
            }
        }
        return Observable.just(cache)
    }

    override
    fun add(element: User) {
        db.use {
            insertWithOnConflict(TABLE_NAME, null, UserToCvMapper.map(element), SQLiteDatabase.CONFLICT_IGNORE)
        }
        cache.add(element)
    }

    override
    fun add(elements: List<User>) {
        async() {
            db.writableDatabase.transaction {
                elements.forEach {
                    insertWithOnConflict(TABLE_NAME, null, UserToCvMapper.map(it), SQLiteDatabase.CONFLICT_IGNORE)
                }
            }
            cache.addAll(elements)
        }
    }

    override
    fun remove(element: User) {
        db.use {
            delete(TABLE_NAME, "${DatabaseContract.Friend.COLUMN_ID} = ?s", arrayOf(element.userID))
        }
        cache.remove(element)
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
