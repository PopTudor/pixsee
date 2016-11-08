package com.marked.pixsee.ui.authentification;

import com.google.gson.JsonObject;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.networking.ServerConstants;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
interface AuthAPI {
	String SERVER_SIGNUP_USER = ServerConstants.SIGNUP;

	@FormUrlEncoded
	@POST(ServerConstants.USER)
	Observable<Response<JsonObject>> create(@Field("name") String name, @Field("email") String email,
	                                        @Field("userName") String username, @Field("password") String password,
	                                        @Field("token") String token);

	@FormUrlEncoded
	@GET(ServerConstants.USER)
	Call<JsonObject> read(@Query("email") String email);


	@PUT(ServerConstants.USER)
	Call<JsonObject> update(@Body User friend);

	@DELETE(ServerConstants.USER)
	Call<JsonObject> delete(@Body User friend);

	@HEAD(SERVER_SIGNUP_USER)
	Observable<Response<Void>> checkUsername(@Query("userName") String userName);

	@HEAD(SERVER_SIGNUP_USER)
	Observable<Response<Void>> hasAccount(@Query("email") String email);

	@POST(ServerConstants.SERVER_LOGIN)
	Observable<Response<JsonObject>> login(@Body User user);
}
