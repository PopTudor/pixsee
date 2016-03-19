package com.marked.pixsee.friends;

import com.marked.pixsee.data.Repository;
import com.marked.pixsee.friends.FriendsContract.View;
import com.marked.pixsee.friends.data.Friend;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import static com.facebook.common.internal.Preconditions.checkNotNull;


/**
 * Created by Tudor Pop on 15-Mar-16.
 */
public class FriendPresenter implements FriendsContract.UserActionsListener {
	private WeakReference<View> mView;
	private Repository repository;

	public FriendPresenter(@NotNull Repository repository, @NotNull View view) {
		this.repository = checkNotNull(repository, "Repository must not be null");
		this.mView = checkNotNull(new WeakReference<>(view),view.getClass().getCanonicalName()+" has not implemented: "+View.class.getCanonicalName());
	}

	@Override
	public void loadFriends(boolean forceUpdate) {
		if (forceUpdate) {
			repository.clear();
		}
		loadFriends(10);
	}

	@Override
	public void loadFriends(int num) {
		int size = repository.length();
		repository.loadMore(num);
		mView.get().onFriendsLoaded(size, repository.length());
	}

	@Override
	public void openFriendDetailUI(@NotNull Friend friend) {
		mView.get().showFriendDetailUI(friend);
	}

}
