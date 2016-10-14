package com.marked.pixsee.features.chat;

import com.marked.pixsee.di.components.FakeActivityComponent;
import com.marked.pixsee.di.scopes.FragmentScope;

import dagger.Component;

/**
 * Created by Tudor on 13-Oct-16.
 */
@Component(modules = FakeChatModule.class, dependencies = FakeActivityComponent.class)
@FragmentScope
interface FakeChatComponent {
}
