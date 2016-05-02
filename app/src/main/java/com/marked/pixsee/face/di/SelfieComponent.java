package com.marked.pixsee.face.di;

import com.marked.pixsee.injection.scopes.PerActivity;

import dagger.Component;

/**
 * Created by Tudor on 4/24/2016.
 */
@PerActivity
@Component(modules = {SelfieModule.class})
public interface SelfieComponent {
//	void inject(SelfieActivity selfieActivity);
}
