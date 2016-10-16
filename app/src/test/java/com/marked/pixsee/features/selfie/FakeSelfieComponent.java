package com.marked.pixsee.features.selfie;

import com.marked.pixsee.di.components.ActivityComponent;
import com.marked.pixsee.di.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by tudor on 10.08.2016.
 */
@Component(modules = FakeSelfieModule.class, dependencies = ActivityComponent.class)
@FragmentScope
interface FakeSelfieComponent extends SelfieComponent {

}