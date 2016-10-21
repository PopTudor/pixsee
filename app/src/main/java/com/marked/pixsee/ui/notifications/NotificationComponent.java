package com.marked.pixsee.ui.notifications;

import com.marked.pixsee.injection.components.AppComponent;
import com.marked.pixsee.injection.scopes.UserScope;

import dagger.Component;

/**
 * Created by Tudor on 16-Oct-16.
 */
@UserScope
@Component(dependencies = AppComponent.class)
interface NotificationComponent {
	void inject(NewMessageNotification newMessageNotification);
}
