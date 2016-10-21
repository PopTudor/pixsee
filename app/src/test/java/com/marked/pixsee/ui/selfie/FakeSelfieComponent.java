package com.marked.pixsee.ui.selfie;

import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by tudor on 10.08.2016.
 */
@Component(modules = FakeSelfieModule.class, dependencies = ActivityComponent.class)
@FragmentScope
interface FakeSelfieComponent extends SelfieComponent {

}