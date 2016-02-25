package com.marked.pixsee.networking;

import com.google.gson.JsonArray;
import com.marked.pixsee.extra.ServerConstants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public interface FriendsAPI {
	@GET(ServerConstants.FRIENDS)
	Call<JsonArray> listFriends(@Query("id") String userId);
}
