package com.marked.vifo.extra;

/**
 * Created by Tudor Pop on 06-Dec-15.
 * https://developers.google.com/cloud-messaging/http-server-ref#downstream
 **/
public interface MessageConstants {

	String TO_TARGETS = "to";
	String FROM_TARGETS = "from";
	String REGISTRATION_IDS_TARGETS = "registration_ids";
	String PRIORITY_OPTION = "priority";
	String COLLAPSE_OPTION = "collapse_key";
	String TIME_TO_LIVE_OPTION = "timeToLive";
	String DELAY_WHILE_IDLE_OPTION = "delayWhileIdle";
	String DRY_RUN_OPTION = "dryRun";
	String RESTRICTED_PACKAGE_NAME_OPTION = "restrictedPackageName";

	String DATA_BODY = "body";
	String DATA_ROOM = "room";
	String DATA_PAYLOAD = "data";
	/**This parameter specifies the predefined, user-visible key-value pairs of the notification payload. */
	String NOTIFICATION_PAYLOAD = "gcm.notification";
	String NOTIFICATION_PAYLOAD_TITLE = NOTIFICATION_PAYLOAD+".title";
	String NOTIFICATION_PAYLOAD_BODY = NOTIFICATION_PAYLOAD+".body";
	String NOTIFICATION_PARAMS_KEY = "notificationParams";


	/*Notification payload support
	* https://developers.google.com/cloud-messaging/http-server-ref#downstream
	* */
	String NOTIFICATION_TITLE = "title";
	String NOTIFICATION_BODY = "body";
	String NOTIFICATION_ICON = "icon";
	String NOTIFICATION_SOUND = "sound";
	String NOTIFICATION_TAG = "tag";
	String NOTIFICATION_COLOR = "color";
	String NOTIFICATION_ACTION_CLICK = "click_action";
	String NOTIFICATION_TITLE_LOCK_KEY = "title_loc_key";
	String NOTIFICATION_TITLE_LOCK_ARGS = "title_loc_args";
	String NOTIFICATION_BODY_LOCK_KEY = "body_loc_key";
	String NOTIFICATION_BODY_LOCK_ARGS = "body_loc_args";

	String MESSAGE_TYPE = "type";
	interface MessageType {
		int ME = 0, YOU = 1, PHOTO = 2, VIDEO = 3;
	}

}
