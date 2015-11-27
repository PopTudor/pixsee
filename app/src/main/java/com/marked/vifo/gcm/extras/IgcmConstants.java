/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marked.vifo.gcm.extras;

public interface IgcmConstants {
    String SERVER_API_KEY = "AIzaSyDsBUHhZ05Gc8w2y45k7E7s8T509yvawl4";
    String SENDER_ID = "515334962063";
    String SECRET_KEY = "kJ0lUiDoL1lnJqMg";

    String PORT = ":3000";
    String BASE_URL = "http://192.168.0.108";
    String SERVER = BASE_URL + PORT;
    String SERVER_REGISTER_USER = SERVER + "/login";

    String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    /* LOGIN Constants*/ 
    String PASSWORD = "password";
    String EMAIL = "email";
    String NAME = "name";
    String ICONURL = "photo";
    String COVERURL = "coverURL";
}
