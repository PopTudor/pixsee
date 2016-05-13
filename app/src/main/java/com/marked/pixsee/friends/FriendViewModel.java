package com.marked.pixsee.friends;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import com.marked.pixsee.data.User;
import com.marked.pixsee.data.repository.Repository;
import com.marked.pixsee.friends.commands.FabCommand;
import com.marked.pixsee.friends.commands.OpenCameraCommand;
import com.marked.pixsee.friends.data.specifications.GetFriendsSpecification;
import com.marked.pixsee.injection.scopes.PerFragment;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by Tudor Pop on 23-Mar-16.
 */
@PerFragment
public class FriendViewModel {
	private Repository<User> repository;

	public FriendViewModel(Repository<User> repository) {
		this.repository = repository;
	}

	public ObservableInt progressVisibility = new ObservableInt(View.INVISIBLE);
	public ObservableInt recyclerViewVisibility = new ObservableInt(View.INVISIBLE);
	public ObservableField<String> infoMessage = new ObservableField("Click the + button to add a friend");
	public ObservableInt infoMessageVisibility = new ObservableInt(View.INVISIBLE);
	public ObservableArrayList<User> friends = new ObservableArrayList();

	public DataListener dataListener;

	@Inject
	OpenCameraCommand openCamera;
	@Inject
	FabCommand fabCommand;

	void loadFriends(int num) {
		loadFriends(false, num);
	}

	int size = 0;

	public int getSize() {
		return size;
	}

	public void loadFriends(boolean forceUpdate, final int limit) {
		if (forceUpdate) {
			repository.query(new GetFriendsSpecification(0, limit))
					.subscribe(new Action1<List<User>>() {
						           @Override
						           public void call(List<User> users) {
							           if (users.size() > 0)
								           recyclerViewVisibility.set(View.VISIBLE);
							           dataListener.onFriendsLoaded(users, 0, users.size());
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
							           dataListener.onFriendsLoaded(users, size, limit);
							           size += users.size();
						           }
					           }
					);
		}
	}

	public void setDataListener(DataListener dataListener) {
		this.dataListener = dataListener;
	}

	public FabCommand getFabCommand() {
		return fabCommand;
	}

	public OpenCameraCommand getOpenCamera() {
		return openCamera;
	}

	/*This interface is used to send notifications to the view*/
	public interface DataListener {

		/**
		 * Open detail view for a [User]
		 *
		 * @param friend The [User] to show details of
		 */
		void showFriendDetailUI(User friend);

		void onFriendsLoaded(List<User> list, int from, int to);
	}
}
