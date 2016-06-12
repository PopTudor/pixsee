package com.marked.pixsee.profile;

import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.data.repository.user.UserDatasource;
import com.marked.pixsee.data.repository.user.UserRepository;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 07-Jun-16.
 */
public class Presenter implements ProfileContract.Presenter {
	private WeakReference<ProfileContract.View> mViewWeakReference;
	private UserDatasource mUserDatasource;

	public Presenter(ProfileContract.View viewWeakReference, UserRepository repository) {
		mViewWeakReference = new WeakReference<>(viewWeakReference);
		mUserDatasource = repository;
		mViewWeakReference.get().setPresenter(this);
	}

	@Override
	public void saveAppUser(User user) {
		mUserDatasource.saveAppUser(user);
	}

	@Override
	public void start() {

	}

	@Override
	public void logOut() {
		mUserDatasource.deleteAllUsers();
	}
}
