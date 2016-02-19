package com.marked.vifo.networking;

import com.google.gson.JsonObject;
import com.marked.vifo.extra.ServerConstants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public interface LoginAPI {
    @GET(ServerConstants.SERVER_USER_HAS_ACCOUNT)
    Call<JsonObject> hasAccount(@Query(value = "email") String email);

	@FormUrlEncoded
	@PUT(ServerConstants.USER)
	Call<JsonObject> signUp( @Field(value = "name") String name, @Field(value = "email") String email,
							 @Field(value = "token") String token,@Field(value = "password") String password);
}
