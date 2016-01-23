/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marked.vifo.extra

interface GCMConstants {
	companion object {
		val PACKAGE_NAME = "com.marked.vifo.extras"
		val SERVER_API_KEY = "AIzaSyDsBUHhZ05Gc8w2y45k7E7s8T509yvawl4"
		val SENDER_ID = "515334962063"
		val SECRET_KEY = "kJ0lUiDoL1lnJqMg"
		val SERVER_UPSTREAM_ADRESS = "@gcm.googleapis.com"


		val SENT_TOKEN_TO_SERVER = "sentTokenToServer"
		val USER_ID = "_id"
		val ACTION_SIGNUP = "%s.SIGNUP".format(PACKAGE_NAME)
		val ACTION_LOGIN = "%s.LOGIN".format(PACKAGE_NAME)
		val ACTION_RECOVERY = "%s.ACTION_RECOVERY".format(PACKAGE_NAME)
		val ACTION_ERROR = "%s.ACTION_ERROR".format(PACKAGE_NAME)

		/* ACTION_LOGIN Constants*/
		val PASSWORD = "password"
		val EMAIL = "email"
		val NAME = "name"
		val ICONURL = "photo"
		val COVERURL = "coverURL"

		// DeviceGroup constants
		val NOTIFICATION_KEY_NAME = "notificationKeyName"
		val NOTIFICATION_KEY = "notificationKey"
		val TOKEN = "token"
	}
}
