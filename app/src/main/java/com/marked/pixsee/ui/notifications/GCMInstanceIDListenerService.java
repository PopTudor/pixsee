/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marked.pixsee.ui.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.JsonObject;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.networking.ServerConstants;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;
import rx.Subscriber;

public class GCMInstanceIDListenerService extends FirebaseInstanceIdService {
	private static final String TAG = "***MyInstanceIDLS";
	@Inject
	@Named(ServerConstants.SERVER)
	Retrofit mRetrofit;
	@Inject
	@Named(DatabaseContract.AppsUser.TABLE_NAME)
	User mUser;

	/**
	 * Called if InstanceID token is updated. This may occur if the security of
	 * the previous token had been compromised. This call is initiated by the
	 * InstanceID provider.
	 */
	// [START refresh_token]
	@Override
	public void onTokenRefresh() {
		// Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
		Log.e("***", "onTokenRefresh ");
		String token = FirebaseInstanceId.getInstance().getToken();
		if (mUser == null) return;
		mRetrofit.create(PushService.class)
				.tokenRefresh(token, mUser.getUserID())
				.retry()
				.subscribe(new Subscriber<Response<JsonObject>>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
						Log.d(TAG, "onError: " + e.getMessage());
					}

					@Override
					public void onNext(Response<JsonObject> jsonObjectResponse) {
						if (jsonObjectResponse.isSuccessful())
							Log.d(TAG, "token refreshed");
					}
				});
	}

	// [END refresh_token]
	interface PushService {
		@FormUrlEncoded
		@POST(ServerConstants.SERVER_PUSH_SERVICE)
		Observable<Response<JsonObject>> tokenRefresh(@Field("token") String token, @Field("id") String id);
	}
}
