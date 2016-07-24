package com.marked.pixsee.injection.components;

import com.marked.pixsee.injection.modules.AppModuleFake;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Tudor on 22-Jul-16.
 */
@Singleton
@Component(modules = {AppModuleFake.class})
public interface AppComponentFake extends AppComponent {
	
}