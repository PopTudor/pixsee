package com.marked.pixsee.face.di;

import com.marked.pixsee.face.custom.CameraSource;
import com.marked.pixsee.face.SelfieActivity;
import com.marked.pixsee.injection.scopes.PerActivity;

import dagger.Component;

/**
 * Created by Tudor on 4/24/2016.
 */
@Component(modules = {SelfieModule.class})
@PerActivity
public interface SelfieComponent {
	void inject(SelfieActivity selfieActivity);

	void inject(CameraSource cameraSource);
}
