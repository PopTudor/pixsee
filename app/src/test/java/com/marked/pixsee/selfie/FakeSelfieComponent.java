package com.marked.pixsee.selfie;

import com.pixsee.di.components.FakeActivityComponent;
import com.pixsee.di.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by tudor on 10.08.2016.
 */
@Component(modules = FakeSelfieModule.class, dependencies = FakeActivityComponent.class)
@FragmentScope
interface FakeSelfieComponent extends SelfieComponent {

}