package com.marked.pixsee.main;

import dagger.Component;
import dependencyInjection.modules.FakeActivityModule;
import dependencyInjection.scopes.ActivityScope;

/**
 * Created by Tudor on 23-Jul-16.
 */
@Component(modules = {FakeMainModule.class, FakeActivityModule.class})
@ActivityScope
interface FakeMainComponent extends MainComponent{
	
}