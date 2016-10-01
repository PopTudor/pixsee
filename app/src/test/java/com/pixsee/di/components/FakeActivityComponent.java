package com.pixsee.di.components;

import com.pixsee.di.modules.FakeActivityModule;
import com.pixsee.di.scopes.ActivityScope;

import dagger.Component;

/**
 * Created by tudor on 10.08.2016.
 */
@ActivityScope
@Component(modules = FakeActivityModule.class, dependencies = FakeAppComponent.class)
public interface FakeActivityComponent extends ActivityComponent {
}
