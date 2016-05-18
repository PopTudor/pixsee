package com.marked.pixsee.friends;

import android.view.View;

import com.marked.pixsee.data.User;
import com.marked.pixsee.data.repository.Repository;
import com.marked.pixsee.friends.commands.FabCommand;
import com.marked.pixsee.friends.commands.OpenCameraCommand;
import com.marked.pixsee.friends.data.specifications.GetFriendsSpecification;
import com.marked.pixsee.friends.data.specifications.GetFriendsStartingWith;
import com.marked.pixsee.injection.scopes.PerFragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor Pop on 23-Mar-16.
 */
@PerFragment
public class FriendPresenter {
	private Repository<User> repository;

	private FriendsContract.View mView;

	public FriendPresenter(Repository<User> repository) {
		this.repository = repository;
	}

	@Inject
	OpenCameraCommand openCamera;
	@Inject
	FabCommand fabCommand;

	void loadFriends(int num) {
		loadFriends(false, num);
	}

	void loadFriends(String text, int limit) {
		repository.query(new GetFriendsStartingWith(text, 0, limit))
				.debounce(300, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<User>>() {
					@Override
					public void call(List<User> users) {
						mView.onFriendsReplace(users);
					}
				});
	}

	int size = 0;

	public int getSize() {
		return size;
	}

	public void loadFriends(boolean forceUpdate, final int limit) {
//		repository.add(new User("123", "Pop Tudor", "tudor08pop@yahoo.com", "dea"));
//		repository.add(new User("124", "Sima Ioana", "skumpic_ioana@yahoo.com", "asd"));
//		repository.add(new User("125", "Popa Cristian", "cristipopa@ymail.com", "dsa"));
//		repository.add(new User("126", "Marcel Mirel", "cristipopa@ymail.com", "zxc"));
//		repository.add(new User("127", "Milica Ionut", "cristipopa@ymail.com", "cxz"));
//		repository.add(new User("128", "Andrei Bogdan", "cristipopa@ymail.com", "xcv"));
		if (forceUpdate) {
			repository.query(new GetFriendsSpecification(0, limit))
					.subscribe(new Action1<List<User>>() {
						           @Override
						           public void call(List<User> users) {
							           if (users.size() > 0)
								           mView.setRecyclerViewVisibility(View.VISIBLE);
							           mView.onFriendsInsert(users, 0, users.size());
							           size = users.size();
						           }
					           }
					).unsubscribe();
		} else {
			repository.query(new GetFriendsSpecification(size, limit))
					//                    .flatMap { Observable.from(it) }
					//                    .skip(size)
					//                    .take(limit) /* take unsubscribes automatically */
					//                    .toList()
					.subscribe(new Action1<List<User>>() {
						           @Override
						           public void call(List<User> users) {
							           mView.onFriendsInsert(users, size, limit);
							           size += users.size();
						           }
					           }
					);
		}
	}

	public void setView(FriendsContract.View view) {
		this.mView = view;
	}

	public FabCommand getFabCommand() {
		return fabCommand;
	}

	public OpenCameraCommand getOpenCamera() {
		return openCamera;
	}

}
