package com.marked.pixsee.networking;

import com.marked.pixsee.BuildConfig;

/**
 * Created by Tudor Pop on 03-Jan-16.
 */
public interface ServerConstants {
	String PORT = ":3000/";
	String SCHEME_HTTP = "http://";
	String IP = BuildConfig.SERVER_IP;
	String BASE_URL = SCHEME_HTTP + IP;
	/* ENDPOINTS */
	String USER = "user";
	String USERS = "users";

	String FRIENDS = "friends";
	String FRIENDS_ACCEPTED = FRIENDS+"/accepted";
	String FRIENDS_REJECTED = FRIENDS+"/rejected";
	String FRIENDS_REQUEST = FRIENDS+"/request";
	String MESSAGES = "messages";
	String PUSH_SERVICE = "push/";

	String USER_FRIENDS = USER +"/"+"friends";
	String RECOVERY = "recovery";
	String LOGIN = "login";
	String UPLOAD = "upload";
	String IMAGE = "image";


	String SERVER = BASE_URL + PORT;
	String SERVER_UPLOAD = SERVER+UPLOAD;
	String SERVER_PUSH_SERVICE = PUSH_SERVICE;
	String SERVER_USER = SERVER + USER;
	String SERVER_USER_IMAGE = SERVER + USER+"/"+IMAGE;
	String SERVER_USER_FRIENDS = SERVER_USER + "/" + FRIENDS;
	String SERVER_USER_RECOVERY = SERVER_USER + "/" + RECOVERY;

	String SERVER_USER_LOGIN = SERVER_USER + "/" + LOGIN;
}