package com.marked.pixsee.friends.cards.data;

import com.google.gson.JsonObject;
import com.marked.pixsee.networking.ServerConstants;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Tudor on 2016-05-20.
 */
public interface MessageAPI {
	@GET(ServerConstants.MESSAGES)
	Observable<JsonObject> getMessagesOfFriend(@Query("user_id") String userId, @Query("friend_id") String friendId);
}
