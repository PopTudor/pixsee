package com.marked.pixsee.service.notifications;

import android.app.Notification;

import com.marked.pixsee.service.GCMListenerService;

/**
 * Created by Tudor on 30-Jul-16.
 */
public final class EmptyNotification implements GCMListenerService.FcmNotification {
	@Override
	public Notification buildNotification() {
		return new Notification();
	}
}
