package com.marked.pixsee.main;

import com.marked.pixsee.injection.modules.FakeActivityModule;
import com.marked.pixsee.injection.scopes.ActivityScope;

import dagger.Component;

/**
 * Created by Tudor on 23-Jul-16.
 */
@Component(modules = {FakeMainModule.class, FakeActivityModule.class})
@ActivityScope
interface FakeMainComponent extends MainComponent{
	
}