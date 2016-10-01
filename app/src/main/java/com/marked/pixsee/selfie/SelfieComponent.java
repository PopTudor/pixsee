package com.marked.pixsee.selfie;

import com.pixsee.di.components.ActivityComponent;
import com.pixsee.di.scopes.FragmentScope;

import org.rajawali3d.renderer.Renderer;

import dagger.Component;

/**
 * Created by Tudor on 4/24/2016.
 */
@Component(modules = {SelfieModule.class}, dependencies = ActivityComponent.class)
@FragmentScope
interface SelfieComponent {
	void inject(SelfieFragment selfieFragment);

	void inject(Renderer renderer);
}
