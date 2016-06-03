package com.marked.pixsee.friendsInvite.addUsername.data;

import com.google.gson.JsonObject;
import com.marked.pixsee.networking.ServerConstants;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Tudor on 03-Jun-16.
 */
public interface AddUserAPI {
	@GET(ServerConstants.USERS)
	Observable<JsonObject> getFriendsWithEmail(@Query("email") String email);
}
