package com.marked.vifo.networking;

import com.google.gson.JsonObject;
import com.marked.vifo.extra.ServerConstants;
import com.marked.vifo.model.contact.Contact;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public interface LoginAPI {
    @POST(ServerConstants.USER)
    Call<JsonObject> create(@Body Contact contact);

    @GET(ServerConstants.USER)
    Call<JsonObject> read(@Query(value = "email") String email);

    @PUT(ServerConstants.USER)
    Call<JsonObject> update(@Body Contact contact);

    @DELETE(ServerConstants.USER)
    Call<JsonObject> delete(@Body Contact contact);

    @HEAD(ServerConstants.USER)
    Call<Void> hasAccount(@Query(value = "email") String email);

}
