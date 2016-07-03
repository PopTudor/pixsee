package com.marked.pixsee.profile;

import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 07-Jun-16.
 */
class Presenter implements ProfileContract.Presenter {
	private WeakReference<ProfileContract.View> mViewWeakReference;
	private UserDatasource mUserDatasource;
	private File mPublicPictureDirectoryFile;

	public Presenter(ProfileContract.View viewWeakReference, UserDatasource repository, File publicPictureDirectoryFile) {
		mPublicPictureDirectoryFile = publicPictureDirectoryFile;
		mViewWeakReference = new WeakReference<>(viewWeakReference);
		mUserDatasource = repository;
		mViewWeakReference.get().setPresenter(this);
	}

	@Override
	public void saveAppUser(User user) {
		mUserDatasource.saveAppUser(user);
	}

	@Override
	public void attach() {
		File[] list = mPublicPictureDirectoryFile.listFiles();
		List<String> strings = new ArrayList<>(list.length);
		for(File it : list)
			strings.add(it.getAbsolutePath());

		mViewWeakReference.get().setData(strings);
	}

	@Override
	public void logOut() {
		mUserDatasource.deleteAllUsers();
	}



	@Override
	public void inviteFriendsClicked() {
		mViewWeakReference.get().showFriendsInvite();
	}
}
