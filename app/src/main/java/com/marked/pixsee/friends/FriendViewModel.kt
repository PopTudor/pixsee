package com.marked.pixsee.friends

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import com.marked.pixsee.data.User
import com.marked.pixsee.di.scopes.PerFragment
import com.marked.pixsee.friends.commands.FabCommand
import com.marked.pixsee.friends.commands.OpenCameraCommand
import com.marked.pixsee.friends.data.FriendRepository
import rx.Observable

/**
 * Created by Tudor Pop on 23-Mar-16.
 */
@PerFragment
class FriendViewModel(private val repository: FriendRepository) {
    @JvmField val progressVisibility: ObservableInt = ObservableInt(View.INVISIBLE)
    @JvmField val recyclerViewVisibility: ObservableInt = ObservableInt(View.INVISIBLE)
    @JvmField val infoMessage: ObservableField<String> = ObservableField("Click the + button to add a friend")
    @JvmField val infoMessageVisibility: ObservableInt = ObservableInt(View.INVISIBLE)
    @JvmField val friends: ObservableArrayList<User> = ObservableArrayList()

    var clickCallback: DataListener? = null

    lateinit var openCamera: OpenCameraCommand
    lateinit var fabCommand: FabCommand

    fun onFriendsLoaded(forceUpdate: Boolean) {
        if (forceUpdate) {
            repository.clear()
        }
        onFriendsLoaded(10)
    }

    fun onFriendsLoaded(num: Int) {
        val size = repository.length()
        repository.loadMore(num)
        Observable.from(repository).skip(size).subscribe { e ->
            friends.add(e)
        }
    }

    /*This interface is used to send notifications to the view*/
    interface DataListener {

        /**
         * Open detail view for a [User]

         * @param friend The [User] to show details of
         */
        fun showFriendDetailUI(friend: User)

        fun onFriendsLoaded(from: Int, to: Int)
    }
}
