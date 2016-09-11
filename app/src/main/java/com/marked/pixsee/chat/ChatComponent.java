package com.marked.pixsee.chat;

import dagger.Component;
import dependencyInjection.components.ActivityComponent;
import dependencyInjection.scopes.FragmentScope;

/**
 * Created by Tudor on 2016-05-28.
 */
@Component(modules = ChatModule.class,dependencies = {ActivityComponent.class})
@FragmentScope
interface ChatComponent {
	void inject(ChatFragment chatFragment);
}
