package com.marked.vifo.networking;

import com.google.gson.JsonObject;
import com.marked.vifo.extra.ServerConstants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public interface FriendsAPI {
	@GET(ServerConstants.USER + "/" + ServerConstants.FRIENDS)
	Call<JsonObject> listFriends(@Query("id") String id);
}
