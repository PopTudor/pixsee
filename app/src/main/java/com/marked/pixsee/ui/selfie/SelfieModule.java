package com.marked.pixsee.ui.selfie;

import android.view.TextureView;

import com.marked.pixsee.camerasource.PixseeCamera;
import com.marked.pixsee.injection.scopes.FragmentScope;

import java.lang.ref.WeakReference;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 4/24/2016.
 */
@Module
@FragmentScope
class SelfieModule {
	private WeakReference<SelfieContract.View> mSelfieFragment;

	SelfieModule(SelfieContract.View selfieFragment) {
		this.mSelfieFragment = new WeakReference<>(selfieFragment);
	}

	@Provides
	@FragmentScope
	@Named(value = "cameraTexture")
	TextureView.SurfaceTextureListener provideCameraTexture(SelfieContract.Presenter presenter) {
		return new SelfieFragment.CameraTextureAvailable(presenter);
	}

	@Provides
	@FragmentScope
	SelfieContract.Presenter provideFacePresenter(PixseeCamera camera) {
		return new FacePresenter(mSelfieFragment.get(), camera);
	}

	@Provides
	@FragmentScope
	PixseeCamera provideCamera() {
		return new PixseeCamera() {
		};
	}

}
