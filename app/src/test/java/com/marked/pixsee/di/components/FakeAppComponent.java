package com.marked.pixsee.di.components;

import com.marked.pixsee.di.modules.FakeAppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Tudor on 22-Jul-16.
 */
@Singleton
@Component(modules = {FakeAppModule.class})
public interface FakeAppComponent extends AppComponent {
	
}