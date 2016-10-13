package com.marked.pixsee.chat;

import com.marked.pixsee.di.components.ActivityComponent;
import com.marked.pixsee.di.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by Tudor on 2016-05-28.
 */
@Component(modules = ChatModule.class,dependencies = {ActivityComponent.class})
@FragmentScope
interface ChatComponent {
	void inject(ChatFragment chatFragment);
}
