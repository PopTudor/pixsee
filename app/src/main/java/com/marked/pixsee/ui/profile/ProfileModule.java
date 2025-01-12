package com.marked.pixsee.ui.profile;

import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.injection.scopes.Repository;
import com.marked.pixsee.utility.BitmapUtils;

import java.io.File;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 07-Jun-16.
 */
@Module
class ProfileModule {
	private ProfileContract.View mViewWeakReference;

	public ProfileModule(ProfileContract.View view) {
		mViewWeakReference = view;
	}
	@FragmentScope
	@Provides
	ProfileContract.Presenter providePresenter(@Repository UserDatasource userRepository, UserManager userManager) {
		File publicPictureDirectory = BitmapUtils.getPublicPictureDirectory();
		if (publicPictureDirectory==null)
			publicPictureDirectory = new File(""); //// TODO: 16-Jul-16 is this correct ? or it will return the root of the file system ?
		return new Presenter(mViewWeakReference, userRepository, userManager, publicPictureDirectory);
	}
}
