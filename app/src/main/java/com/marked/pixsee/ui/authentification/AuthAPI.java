package com.marked.pixsee.ui.authentification;

import com.google.gson.JsonObject;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.networking.ServerConstants;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HEAD;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
interface AuthAPI {
	String SERVER_SIGNUP_USER = ServerConstants.SIGNUP;

	@FormUrlEncoded
	@POST(SERVER_SIGNUP_USER)
	Observable<Response<JsonObject>> create(@Field("name") String name, @Field("email") String email,
	                                        @Field("userName") String username, @Field("password") String password,
	                                        @Field("pushToken") String pushToken);

	@HEAD(SERVER_SIGNUP_USER)
	Observable<Response<Void>> hasAccount(@Query("email") String email);

	@POST(ServerConstants.LOGIN)
	Observable<Response<JsonObject>> login(@Body User user);
}
