package com.marked.pixsee.profile;

import com.marked.pixsee.data.repository.user.UserDatasource;
import com.marked.pixsee.injection.Repository;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.utility.BitmapUtils;

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
	ProfileContract.Presenter providePresenter(@Repository UserDatasource userRepository){
		return new Presenter(mViewWeakReference, userRepository, BitmapUtils.getPublicPictureDirectory());
	}
}
