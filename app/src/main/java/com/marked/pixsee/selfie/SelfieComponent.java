package com.marked.pixsee.selfie;

import org.rajawali3d.renderer.Renderer;

import dagger.Component;
import dependencyInjection.components.ActivityComponent;
import dependencyInjection.scopes.FragmentScope;

/**
 * Created by Tudor on 4/24/2016.
 */
@Component(modules = {SelfieModule.class}, dependencies = ActivityComponent.class)
@FragmentScope
interface SelfieComponent {
	void inject(SelfieFragment selfieFragment);

	void inject(Renderer renderer);
}
