package com.marked.pixsee.profile.di;

import com.marked.pixsee.data.repository.user.UserRepository;
import com.marked.pixsee.injection.scopes.PerFragment;
import com.marked.pixsee.profile.Presenter;
import com.marked.pixsee.profile.ProfileContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 07-Jun-16.
 */
@Module
public class ProfileModule {
	private ProfileContract.View mViewWeakReference;

	public ProfileModule(ProfileContract.View view) {
		mViewWeakReference = view;
	}
	@PerFragment
	@Provides
	ProfileContract.Presenter providePresenter(UserRepository userRepository){
		return new Presenter(mViewWeakReference, userRepository);
	}
}
