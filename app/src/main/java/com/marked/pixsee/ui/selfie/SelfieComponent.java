package com.marked.pixsee.ui.selfie;

import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.scopes.FragmentScope;

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
