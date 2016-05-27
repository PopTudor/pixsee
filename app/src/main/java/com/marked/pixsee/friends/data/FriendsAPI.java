package com.marked.pixsee.friends.data;

import com.google.gson.JsonObject;
import com.marked.pixsee.networking.ServerConstants;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public interface FriendsAPI {
	@GET(ServerConstants.FRIENDS)
    Observable<JsonObject> getFriendsOfUser(@Query("id") String userId);
}
