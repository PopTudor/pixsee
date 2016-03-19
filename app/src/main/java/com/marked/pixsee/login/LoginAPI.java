package com.marked.pixsee.login;

import com.google.gson.JsonObject;
import com.marked.pixsee.data.User;
import com.marked.pixsee.networking.ServerConstants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public interface LoginAPI {
    @FormUrlEncoded
    @POST(ServerConstants.USER)
    Call<JsonObject> create(@Field("name") String name, @Field("email") String email, @Field("token") String token, @Field("password") String password);

    @GET(ServerConstants.USER)
    Call<JsonObject> read(@Query("email") String email);

    @PUT(ServerConstants.USER)
    Call<JsonObject> update(@Body User friend);

    @DELETE(ServerConstants.USER)
    Call<JsonObject> delete(@Body User friend);

    @HEAD(ServerConstants.USER)
    Call<Void> hasAccount(@Query("email") String email);

    @FormUrlEncoded
    @POST(ServerConstants.LOGIN)
    Call<JsonObject> login(@Field("email") String email, @Field("password") String password, @Field("token") String token);
}
