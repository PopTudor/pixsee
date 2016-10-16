package com.marked.pixsee.features.main;

import com.marked.pixsee.di.modules.ActivityModule;
import com.marked.pixsee.di.scopes.ActivityScope;

import dagger.Component;

/**
 * Created by Tudor on 23-Jul-16.
 */
@Component(modules = {FakeMainModule.class, ActivityModule.class})
@ActivityScope
interface FakeMainComponent extends MainComponent{
	
}