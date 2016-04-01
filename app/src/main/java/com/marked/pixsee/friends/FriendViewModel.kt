package com.marked.pixsee.friends

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import com.marked.pixsee.data.User
import com.marked.pixsee.data.repository.Repository
import com.marked.pixsee.di.scopes.PerFragment
import com.marked.pixsee.friends.commands.FabCommand
import com.marked.pixsee.friends.commands.OpenCameraCommand
import com.marked.pixsee.friends.data.specifications.GetFriendsSpecification
import javax.inject.Inject

/**
 * Created by Tudor Pop on 23-Mar-16.
 */
@PerFragment
class FriendViewModel(private val repository: Repository<User>) {
    @JvmField val progressVisibility: ObservableInt = ObservableInt(View.INVISIBLE)
    @JvmField val recyclerViewVisibility: ObservableInt = ObservableInt(View.INVISIBLE)
    @JvmField val infoMessage: ObservableField<String> = ObservableField("Click the + button to add a friend")
    @JvmField val infoMessageVisibility: ObservableInt = ObservableInt(View.INVISIBLE)
    @JvmField val friends: ObservableArrayList<User> = ObservableArrayList()

    lateinit var dataListener: DataListener

    @Inject
    lateinit var openCamera: OpenCameraCommand
    @Inject
    lateinit var fabCommand: FabCommand

    fun loadFriends(num: Int = 50) {
        loadFriends(false, num)
    }

    var size = 0;

    fun loadFriends(forceUpdate: Boolean, limit: Int) {
        if (forceUpdate) {
            repository.query(GetFriendsSpecification(0, limit))
                    .subscribe { users: List<User> ->
                        if (users.size > 0)
                            recyclerViewVisibility.set(View.VISIBLE)
                        dataListener.onFriendsLoaded(users, 0, users.size)
                        size = users.size
                    }.unsubscribe()
        } else {
            repository.query(GetFriendsSpecification(size, limit))
                    //                    .flatMap { Observable.from(it) }
                    //                    .skip(size)
                    //                    .take(limit) /* take unsubscribes automatically */
                    //                    .toList()
                    .subscribe { users: List<User> ->
                        dataListener.onFriendsLoaded(users, size, limit)
                        size += users.size
                    }
        }
    }

    /*This interface is used to send notifications to the view*/
    interface DataListener {

        /**
         * Open detail view for a [User]

         * @param friend The [User] to show details of
         */
        fun showFriendDetailUI(friend: User)

        fun onFriendsLoaded(list: List<User>, from: Int, to: Int)
    }
}
