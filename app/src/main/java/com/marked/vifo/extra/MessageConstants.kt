package com.marked.vifo.extra

/**
 * Created by Tudor Pop on 06-Dec-15.
 * https://developers.google.com/cloud-messaging/http-server-ref#downstream
 */
interface MessageConstants {
	interface MessageType {
		companion object {
			const val ME = 0
			const val YOU = 1
			const val PHOTO = 2
			const val VIDEO = 3
			const val TYPING = 4
		}
	}

	companion object {
		const val TO = "to" /* message destination, (destination is a better name but GCM uses to)*/
		const val FROM = "from"  /* source of the message (the app's user id)*/
		const val REGISTRATION_IDS_TARGETS = "registration_ids"
		const val PRIORITY_OPTION = "priority"
		const val COLLAPSE_OPTION = "collapse_key"
		const val TIME_TO_LIVE_OPTION = "timeToLive"
		const val DELAY_WHILE_IDLE_OPTION = "delayWhileIdle"
		const val RESTRICTED_PACKAGE_NAME_OPTION = "restrictedPackageName"

		/* This parameter specifies the predefined key-value pairs of the data payload */
		const val DATA_PAYLOAD = "data" /* JSON object that holds the body */
		const val DATA_BODY = "body"
		const val CHAT_ROOM = "room"
		const val CREATION_DATE = "date"
		/**This parameter specifies the predefined, user-visible key-value pairs of the notification payload.  */
		const val NOTIFICATION_PAYLOAD = "gcm.notification"
		const val NOTIFICATION_PAYLOAD_TITLE = NOTIFICATION_PAYLOAD + ".title"
		const val NOTIFICATION_PAYLOAD_BODY = NOTIFICATION_PAYLOAD + ".body"
		const val MESSAGE_TYPE = "type"

		/*
		* Notification payload support
		* https://developers.google.com/cloud-messaging/http-server-ref#downstream
		**/
		const val NOTIFICATION_PARAMS_KEY = "notificationParams"
		const val NOTIFICATION_TITLE = "title"
		const val NOTIFICATION_BODY = "body"
		const val NOTIFICATION_ICON = "icon"
		const val NOTIFICATION_SOUND = "sound"
		const val NOTIFICATION_TAG = "tag"
		const val NOTIFICATION_COLOR = "color"
		const val NOTIFICATION_ACTION_CLICK = "click_action"
		const val NOTIFICATION_TITLE_LOCK_KEY = "title_loc_key"
		const val NOTIFICATION_TITLE_LOCK_ARGS = "title_loc_args"
		const val NOTIFICATION_BODY_LOCK_KEY = "body_loc_key"

		const val NOTIFICATION_BODY_LOCK_ARGS = "body_loc_args"
	}
}