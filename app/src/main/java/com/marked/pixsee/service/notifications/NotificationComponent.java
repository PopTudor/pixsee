package com.marked.pixsee.service.notifications;

import com.marked.pixsee.di.components.AppComponent;
import com.marked.pixsee.di.scopes.UserScope;

import dagger.Component;

/**
 * Created by Tudor on 16-Oct-16.
 */
@UserScope
@Component(dependencies = AppComponent.class)
interface NotificationComponent {
	void inject(NewMessageNotification newMessageNotification);
}
