package com.marked.pixsee.main;

import com.google.gson.JsonObject;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;

import java.lang.ref.WeakReference;

import rx.functions.Action1;

/**
 * Created by Tudor on 2016-05-27.
 */
class MainPresenter implements MainContract.Presenter {
	private WeakReference<MainContract.View> mWeakView;
	private UserDatasource mRepository;

	public MainPresenter(MainContract.View view, UserDatasource userDatasource) {
		this.mRepository = userDatasource;
		this.mWeakView = new WeakReference<>(view);
		this.mWeakView.get().setPresenter(this);
	}

	@Override
	public void chatTabClicked() {
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
	public void cameraTabClicked() {
		mWeakView.get().hideBottomNavigation();
		mWeakView.get().showCamera();
	}

	@Override
	public void attach() {
		chatTabClicked();
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
	public void profileTabClicked() {
		User user = mRepository.getUser(DatabaseContract.AppsUser.TABLE_NAME);
		mWeakView.get().showProfile(user);
	}
}
