package com.marked.pixsee.chat;

import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by Tudor on 2016-05-28.
 */
@Component(modules = ChatModule.class,dependencies = {ActivityComponent.class})
@FragmentScope
interface ChatComponent {
	void inject(ChatFragment chatFragment);
}
