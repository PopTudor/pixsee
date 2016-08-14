package com.marked.pixsee.selfie;

import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.scopes.FragmentScope;
import com.marked.pixsee.selfie.camerasource.CameraSource;

import dagger.Component;

/**
 * Created by Tudor on 4/24/2016.
 */
@Component(modules = {SelfieModule.class}, dependencies = ActivityComponent.class)
@FragmentScope
interface SelfieComponent {
	void inject(SelfieFragment selfieFragment);

	void inject(CameraSource cameraSource);
}
