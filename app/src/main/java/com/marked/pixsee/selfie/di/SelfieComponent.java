package com.marked.pixsee.selfie.di;

import com.marked.pixsee.selfie.custom.CameraSource;
import com.marked.pixsee.selfie.SelfieFragment;
import com.marked.pixsee.injection.scopes.ActivityScope;

import dagger.Component;

/**
 * Created by Tudor on 4/24/2016.
 */
@Component(modules = {SelfieModule.class})
@ActivityScope
public interface SelfieComponent {
	void inject(SelfieFragment selfieFragment);

	void inject(CameraSource cameraSource);
}
