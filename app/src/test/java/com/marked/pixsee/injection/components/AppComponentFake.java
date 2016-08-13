package com.marked.pixsee.injection.components;

import com.marked.pixsee.injection.modules.FakeAppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Tudor on 22-Jul-16.
 */
@Singleton
@Component(modules = {FakeAppModule.class})
public interface AppComponentFake extends AppComponent {
	
}