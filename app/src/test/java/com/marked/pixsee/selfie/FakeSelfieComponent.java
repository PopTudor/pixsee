package com.marked.pixsee.selfie;

import dagger.Component;
import dependencyInjection.components.FakeActivityComponent;
import dependencyInjection.scopes.FragmentScope;

/**
 * Created by tudor on 10.08.2016.
 */
@Component(modules = FakeSelfieModule.class, dependencies = FakeActivityComponent.class)
@FragmentScope
interface FakeSelfieComponent extends SelfieComponent {

}