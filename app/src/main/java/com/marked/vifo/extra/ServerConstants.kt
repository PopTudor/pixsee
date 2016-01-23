package com.marked.vifo.extra

/**
 * Created by Tudor Pop on 03-Jan-16.
 */
interface ServerConstants {
	companion object {
		val PORT = ":3000"
		val BASE_URL =
				"http://192.168.0.101"
		//						"http://192.168.0.107"; /* CAMIN */
		//			"http://46.101.168.55"; /* DIGITAL-OCEAN */
		val SERVER = BASE_URL + PORT
		val SERVER_USER = SERVER + "/user"
		val SERVER_USER_FRIENDS = SERVER_USER + "/friends"
		val SERVER_USER_EXISTS = SERVER_USER + "/exists"
		val SERVER_USER_RECOVERY = SERVER_USER + "/recovery"
		val SERVER_USER_LOGIN = SERVER_USER + "/login"
	}
}