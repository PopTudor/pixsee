package com.marked.vifo.extra

/**
 * Created by Tudor Pop on 06-Dec-15.
 * https://developers.google.com/cloud-messaging/http-server-ref#downstream
 */
interface MessageConstants {
	interface MessageType {
		companion object {
			val ME = 0
			val YOU = 1
			val PHOTO = 2
			val VIDEO = 3
			val TYPING = 4
		}
	}

	companion object {
		val TO_TARGETS = "to"
		val FROM_TARGETS = "from"
		val REGISTRATION_IDS_TARGETS = "registration_ids"
		val PRIORITY_OPTION = "priority"
		val COLLAPSE_OPTION = "collapse_key"
		val TIME_TO_LIVE_OPTION = "timeToLive"
		val DELAY_WHILE_IDLE_OPTION = "delayWhileIdle"
		val DRY_RUN_OPTION = "dryRun"
		val RESTRICTED_PACKAGE_NAME_OPTION = "restrictedPackageName"

		val DATA_BODY = "body"
		val DATA_ROOM = "room"
		val DATA_PAYLOAD = "data"
		/**This parameter specifies the predefined, user-visible key-value pairs of the notification payload.  */
		val NOTIFICATION_PAYLOAD = "gcm.notification"
		val NOTIFICATION_PAYLOAD_TITLE = NOTIFICATION_PAYLOAD + ".title"
		val NOTIFICATION_PAYLOAD_BODY = NOTIFICATION_PAYLOAD + ".body"
		val NOTIFICATION_PARAMS_KEY = "notificationParams"


		/*Notification payload support
	* https://developers.google.com/cloud-messaging/http-server-ref#downstream
	* */
		val NOTIFICATION_TITLE = "title"
		val NOTIFICATION_BODY = "body"
		val NOTIFICATION_ICON = "icon"
		val NOTIFICATION_SOUND = "sound"
		val NOTIFICATION_TAG = "tag"
		val NOTIFICATION_COLOR = "color"
		val NOTIFICATION_ACTION_CLICK = "click_action"
		val NOTIFICATION_TITLE_LOCK_KEY = "title_loc_key"
		val NOTIFICATION_TITLE_LOCK_ARGS = "title_loc_args"
		val NOTIFICATION_BODY_LOCK_KEY = "body_loc_key"
		val NOTIFICATION_BODY_LOCK_ARGS = "body_loc_args"

		val MESSAGE_TYPE = "type"
	}
}