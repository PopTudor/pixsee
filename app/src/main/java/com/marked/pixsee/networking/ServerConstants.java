package com.marked.pixsee.networking;

/**
 * Created by Tudor Pop on 03-Jan-16.
 */
public interface ServerConstants {
	String PORT = ":3000/";
	String BASE_URL =
//			"http://192.168.0.105";
			"http://192.168.0.101";
//			"http://172.20.10.10";
//				"http://46.101.182.220"; /* DIGITAL-OCEAN */
	/* ENDPOINTS */
	String USER = "user";
	String USERS = "users";

	String FRIENDS = "friends";
	String FRIENDS_ACCEPTED = FRIENDS+"/accepted";
	String FRIENDS_REJECTED = FRIENDS+"/rejected";
	String MESSAGES = "messages";
	String USER_FRIENDS = USER +"/"+"friends";
	String RECOVERY = "recovery";
	String LOGIN = "login";


	String SERVER = BASE_URL + PORT;
	String SERVER_USER = SERVER + USER;
	String SERVER_USER_FRIENDS = SERVER_USER + "/" + FRIENDS;
	String SERVER_USER_RECOVERY = SERVER_USER + "/" + RECOVERY;

	String SERVER_USER_LOGIN = SERVER_USER + "/" + LOGIN;
}