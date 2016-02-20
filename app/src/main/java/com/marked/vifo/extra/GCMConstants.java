package com.marked.vifo.extra;

public interface GCMConstants {
	String PACKAGE_NAME = "com.marked.vifo.extras";
	String SERVER_API_KEY = "AIzaSyDsBUHhZ05Gc8w2y45k7E7s8T509yvawl4";
	String SENDER_ID = "515334962063";
	String SECRET_KEY = "kJ0lUiDoL1lnJqMg";
	String SERVER_UPSTREAM_ADRESS = "@gcm.googleapis.com";


	String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
	String USER_ID = UserConstants.ID;
	String FRIENDS = "friends";
	String ACTION_SIGNUP = String.format("%s.SIGNUP", PACKAGE_NAME);
	String ACTION_LOGIN = String.format("%s.LOGIN", PACKAGE_NAME);
	String ACTION_RECOVERY = String.format("%s.ACTION_RECOVERY", PACKAGE_NAME);
	String ACTION_ERROR = PACKAGE_NAME+".ACTION_ERROR";

	/* ACTION_LOGIN Constants*/ String PASSWORD = "password";
	String EMAIL = "email";
	String NAME = "name";
	String ICONURL = "photo";
	String COVERURL = "coverURL";

	// DeviceGroup constants
	String NOTIFICATION_KEY_NAME = "notificationKeyName";
	String NOTIFICATION_KEY = "notificationKey";
	String TOKEN = "token";
}
