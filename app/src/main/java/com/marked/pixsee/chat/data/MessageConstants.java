package com.marked.pixsee.chat.data;


/**
 * Created by Tudor Pop on 06-Dec-15.
 * https://developers.google.com/cloud-messaging/http-server-ref#downstream
 * {
 * "to" : "APA91bHun4MxP5egoKMwt2KZFBaFUH-1RYqx...",
 * "notification" : {
 * "body" : "great match!",
 * "title" : "Portugal vs. Denmark",
 * "icon" : "myicon"
 * },
 * "data" : {
 * "Nick" : "Mario",
 * "Room" : "PortugalVSDenmark"
 * }
 * }
 */
public interface MessageConstants {
	String TO = "to"; /* message destination, (destination is a better name but GCM uses to)*/
	String ID = "cloudID"; /* message destination, (destination is a better name but GCM uses to)*/
	String FROM = "from_usr";  /* source of the message (the app's user id)*/
	String REGISTRATION_IDS_TARGETS = "registration_ids";
	String PRIORITY_OPTION = "priority";
	String COLLAPSE_OPTION = "collapse_key";
	String TIME_TO_LIVE_OPTION = "timeToLive";
	String DELAY_WHILE_IDLE_OPTION = "delayWhileIdle";
	String RESTRICTED_PACKAGE_NAME_OPTION = "restrictedPackageName";
	/* This parameter specifies the predefined key-value pairs of the data payload */
	String DATA_PAYLOAD = "data"; /* JSON object that holds the body {data: {"body": "some nice text" }  } */
	String DATA_BODY = "body";
	String CHAT_ROOM = "room";
	String CREATION_DATE = "date";
	/**
	 * This parameter specifies the predefined, user-visible key-value pairs of the notification payload.
	 */
	String NOTIFICATION_PAYLOAD = "gcm.notification";
	String NOTIFICATION_PAYLOAD_TITLE = NOTIFICATION_PAYLOAD + ".title";
	String NOTIFICATION_PAYLOAD_BODY = NOTIFICATION_PAYLOAD + ".body";
	String MESSAGE_TYPE = "type";
	/*
	* Notification payload support
	* https://developers.google.com/cloud-messaging/http-server-ref#downstream
	**/ String NOTIFICATION_PARAMS_KEY = "notificationParams";
	String NOTIFICATION_TITLE = "title";
	String NOTIFICATION_BODY = "body";
	String NOTIFICATION_ICON = "icon";
	String NOTIFICATION_SOUND = "sound";
	String NOTIFICATION_TAG = "tag";
	String NOTIFICATION_COLOR = "build/intermediates/exploded-aar/com.android.support/appcompat-v7/23.2.0/res/color";
	String NOTIFICATION_ACTION_CLICK = "click_action";
	String NOTIFICATION_TITLE_LOCK_KEY = "title_loc_key";
	String NOTIFICATION_TITLE_LOCK_ARGS = "title_loc_args";
	String NOTIFICATION_BODY_LOCK_KEY = "body_loc_key";
	String NOTIFICATION_BODY_LOCK_ARGS = "body_loc_args";

	interface MessageType {
		int ME_MESSAGE = 0;
		int YOU_MESSAGE = 1;
		int ME_IMAGE = 2;
		int YOU_IMAGE = 3;
		int TYPING = 4;
	}
}