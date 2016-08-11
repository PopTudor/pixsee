package com.marked.pixsee.injection.components;

import com.marked.pixsee.injection.modules.FakeActivityModule;
import com.marked.pixsee.injection.scopes.ActivityScope;

import dagger.Component;

/**
 * Created by tudor on 10.08.2016.
 */
@ActivityScope
@Component(modules = FakeActivityModule.class, dependencies = AppComponentFake.class)
public interface FakeActivityComponent extends ActivityComponent {
}
