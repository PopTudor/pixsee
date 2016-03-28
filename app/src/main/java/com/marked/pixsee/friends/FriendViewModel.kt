package com.marked.pixsee.friends

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import com.marked.pixsee.data.Repository
import com.marked.pixsee.data.User
import com.marked.pixsee.di.scopes.PerFragment
import com.marked.pixsee.friends.commands.FabCommand
import com.marked.pixsee.friends.commands.OpenCameraCommand
import javax.inject.Inject

/**
 * Created by Tudor Pop on 23-Mar-16.
 */
@PerFragment
class FriendViewModel(private val repository: Repository) {
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

    fun loadData(num: Int) {
        loadData(false, num)
    }

    fun loadData(forceUpdate: Boolean = false, num: Int) {
        if (forceUpdate) {
        }
        val size = repository.length()
        val sub =repository.getFriends(num).subscribe { users:List<User>->
            dataListener.onFriendsLoaded(users, size, users.size)
        }
        sub.unsubscribe()
    }

    /*This interface is used to send notifications to the view*/
    interface DataListener {

        /**
         * Open detail view for a [User]

         * @param friend The [User] to show details of
         */
        fun showFriendDetailUI(friend: User)

        fun onFriendsLoaded(list: List<User>,from: Int, to: Int)
    }
}
