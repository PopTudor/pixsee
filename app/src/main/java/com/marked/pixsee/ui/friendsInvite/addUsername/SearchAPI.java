package com.marked.pixsee.ui.friendsInvite.addUsername;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.networking.ServerConstants;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Tudor on 03-Jun-16.
 */
interface SearchAPI {
	@GET(ServerConstants.SEARCH)
	Observable<JsonArray> searchUsersByUsername(@Query("username") String username);

	@Multipart
	@POST(ServerConstants.FRIENDS_REQUEST)
	Observable<Response<JsonObject>> friendRequest(@Part("user") User user, @Part("to") String toToken);
}
