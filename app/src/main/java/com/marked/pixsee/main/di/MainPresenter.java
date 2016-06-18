package com.marked.pixsee.main.di;

import com.google.gson.JsonObject;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.data.repository.user.UserDatasource;
import com.marked.pixsee.data.repository.user.UserRepository;
import com.marked.pixsee.main.MainContract;

import java.lang.ref.WeakReference;

import rx.functions.Action1;

/**
 * Created by Tudor on 2016-05-27.
 */
class MainPresenter implements MainContract.Presenter {
	private WeakReference<MainContract.View> mWeakView;
	private UserDatasource mRepository;

	public MainPresenter(MainContract.View view, UserRepository userDatasource) {
		this.mRepository = userDatasource;
		this.mWeakView = new WeakReference<>(view);
		this.mWeakView.get().setPresenter(this);
	}

	@Override
	public void chatClicked() {
		mWeakView.get().showChat(true);
	}

	@Override
	public void friendRequest(User user, boolean accepted) {
		if (accepted){
			mRepository.saveUser(user)
			.subscribe(new Action1<JsonObject>() {
				@Override
				public void call(JsonObject o) {

				}
			}, new Action1<Throwable>() {
				@Override
				public void call(Throwable throwable) {
					throwable.printStackTrace();
				}
			});
		} else {

		}
	}

	@Override
	public void cameraClicked() {
		mWeakView.get().hideBottomNavigation();
		mWeakView.get().showCamera();
	}

	@Override
	public void attach() {
		chatClicked();
	}

	@Override
	public void friendRequest(User user) {
		mWeakView.get().showFriendRequestDialog(user);
	}

	@Override
	public void execute(Command command) {
		command.execute();
	}

	@Override
	public void profileClicked() {
		User user = mRepository.getUser(DatabaseContract.AppsUser.TABLE_NAME);
		mWeakView.get().showProfile(user);
	}
}
