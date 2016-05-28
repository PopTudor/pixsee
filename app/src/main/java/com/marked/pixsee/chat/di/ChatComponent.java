package com.marked.pixsee.chat.di;

import com.marked.pixsee.chat.ChatFragment;
import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.scopes.PerFragment;

import dagger.Component;

/**
 * Created by Tudor on 2016-05-28.
 */
@Component(modules = ChatModule.class,dependencies = ActivityComponent.class)
@PerFragment
public interface ChatComponent {
	void inject(ChatFragment chatFragment);
}
