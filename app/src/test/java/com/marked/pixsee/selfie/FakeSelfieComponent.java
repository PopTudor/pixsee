package com.marked.pixsee.selfie;

import com.marked.pixsee.injection.components.FakeActivityComponent;
import com.marked.pixsee.injection.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by tudor on 10.08.2016.
 */
@Component(modules = FakeSelfieModule.class, dependencies = FakeActivityComponent.class)
@FragmentScope
interface FakeSelfieComponent extends SelfieComponent {

}