package com.marked.pixsee.friends;

import com.marked.pixsee.data.Repository;
import com.marked.pixsee.data.friend.Friend;
import com.marked.pixsee.friends.FriendsContract.View;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import static com.facebook.common.internal.Preconditions.checkNotNull;


/**
 * Created by Tudor Pop on 15-Mar-16.
 */
public class FriendPresenter implements FriendsContract.UserActionsListener {
    private WeakReference<View> view;
    private Repository repository;

    public FriendPresenter(@NotNull Repository repository, @NotNull View view) {
        this.repository = checkNotNull(repository,"Repository must not be null");
        this.view = checkNotNull(new WeakReference<>(view));
    }

    @Override
    public void loadFriends(boolean forceUpdate) {

    }

    @Override
    public void openFriendDetailUI(@NotNull Friend friend) {
        view.get().showFriendDetailUI(friend);
    }

}
