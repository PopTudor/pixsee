package com.marked.pixsee.main.di;

import com.marked.pixsee.commands.Command;
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
		mWeakView.get().displayChat(true);
	}

	@Override
	public void friendRequest(User user, boolean accepted) {
		if (accepted){
			mRepository.saveUser(user)
			.subscribe(new Action1() {
				@Override
				public void call(Object o) {

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
	public void start() {
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
}
