package com.marked.vifo.extra;

/**
 * Created by Tudor Pop on 03-Jan-16.
 */
public interface ServerConstants {
	String PORT = ":3000";
	String BASE_URL = "http://192.168.0.101";
	//						"http://192.168.0.107"; /* CAMIN */
	//			"http://46.101.168.55"; /* DIGITAL-OCEAN */
	String SERVER = BASE_URL + PORT;
	String SERVER_USER = SERVER + "/user";
	String SERVER_USER_FRIENDS = SERVER_USER + "/friends";
	String SERVER_USER_EXISTS = SERVER_USER + "/exists";
	String SERVER_USER_RECOVERY = SERVER_USER + "/recovery";
	String SERVER_USER_LOGIN = SERVER_USER + "/login";
}