package com.marked.pixsee.ui.profile;

import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.data.user.UserManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 07-Jun-16.
 */
class Presenter implements ProfileContract.Presenter {
	private final WeakReference<ProfileContract.View> mViewWeakReference;
	private final UserDatasource mUserDatasource;
	private final UserManager mUserManager;
	private final File mPublicPictureDirectoryFile;

	public Presenter(ProfileContract.View viewWeakReference, UserDatasource repository, UserManager manager, File publicPictureDirectoryFile) {
		mPublicPictureDirectoryFile = publicPictureDirectoryFile;
		mViewWeakReference = new WeakReference<>(viewWeakReference);
		mUserDatasource = repository;
		this.mUserManager = manager;
		mViewWeakReference.get().setPresenter(this);
	}

	@Override
	public void saveAppUser(User user) {
		mUserManager.saveUser(user);
	}

	@Override
	public void attach() {
		File[] list = mPublicPictureDirectoryFile.listFiles();
		if (list!=null) {
			List<String> strings = new ArrayList<>(list.length);
			for (File it : list)
				strings.add(it.getAbsolutePath());

			mViewWeakReference.get().setData(strings);
		}
	}

	@Override
	public void detach() {

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
