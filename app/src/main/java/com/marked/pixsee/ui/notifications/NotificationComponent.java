package com.marked.pixsee.ui.notifications;

import com.marked.pixsee.injection.components.SessionComponent;
import com.marked.pixsee.injection.scopes.Instance;

import dagger.Component;

/**
 * Created by Tudor on 16-Oct-16.
 */
@Component(dependencies = {SessionComponent.class})
@Instance
interface NotificationComponent {
	void inject(NewMessageNotification newMessageNotification);
}
