package com.marked.pixsee.friends

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import com.marked.pixsee.data.User
import com.marked.pixsee.di.scopes.PerFragment
import com.marked.pixsee.friends.data.FriendRepository
import rx.Observable

/**
 * Created by Tudor Pop on 23-Mar-16.
 */
@PerFragment
class FriendViewModel(private val repository: FriendRepository) : FriendsContract.UserActionsListener {
    @JvmField val progressVisibility: ObservableInt = ObservableInt(View.INVISIBLE)
    @JvmField val recyclerViewVisibility: ObservableInt = ObservableInt(View.INVISIBLE)
    @JvmField val infoMessage: ObservableField<String> = ObservableField("Click the + button to add a friend")
    @JvmField val infoMessageVisibility: ObservableInt = ObservableInt(View.INVISIBLE)
    @JvmField val friends: ObservableArrayList<User> = ObservableArrayList()

    var clickCallback: FriendsContract.UserActionsListener? = null

    override fun onClickCamera(view: View) {
        clickCallback?.onClickCamera(view)
    }

    override fun onClickFab(view: View) {
        clickCallback?.onClickFab(view)
    }

    fun loadFriends(forceUpdate: Boolean) {
        if (forceUpdate) {
            repository.clear()
        }
        loadFriends(10)
    }

    fun loadFriends(num: Int) {
        val size = repository.length()
        repository.loadMore(num)
        Observable.from(repository).skip(size).subscribe { e ->
            friends.add(e)

        }
    }

}
