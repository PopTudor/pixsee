package com.marked.vifo.networking;

import com.google.gson.JsonObject;
import com.marked.vifo.extra.ServerConstants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public interface LoginAPI {
	@GET(ServerConstants.USER + "/" + ServerConstants.EXISTS)
	Call<JsonObject> exists(@Query(value = "email") String email);

	@FormUrlEncoded
	@POST("user")
	Call<JsonObject> signUp(@Field("token") String token, @Field(value = "name") String name,
	                        @Field(value = "email") String email, @Field(value = "password") String password);
}
