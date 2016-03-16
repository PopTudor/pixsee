package com.marked.pixsee.friends;

import com.marked.pixsee.data.friend.Friend;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Tudor Pop on 15-Mar-16.
 * This specifies the contract between the view and the presenter.
 */
interface FriendsContract {
    interface View {
        void showFriendDetailUI(Friend friend);
    }

    interface UserActionsListener {
        void loadFriends(boolean forceUpdate);

        void openFriendDetailUI(@NotNull Friend friend);
    }
}
