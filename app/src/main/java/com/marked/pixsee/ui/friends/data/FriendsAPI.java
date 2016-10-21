package com.marked.pixsee.ui.friends.data;

import com.google.gson.JsonObject;
import com.marked.pixsee.networking.ServerConstants;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public interface FriendsAPI {
	@GET(ServerConstants.FRIENDS)
	Observable<JsonObject> getFriendsOfUser(@Query("id") String userId);

	/**
	 * Sends a request to server in order to accept a friend
	 * @param whoRequested the user that get's accepted as a friend
	 * @param whoAccepts  the user that accepts the friend
	 * @return
	 */
	@POST(ServerConstants.FRIENDS_ACCEPTED)
	Observable<JsonObject> friendAccepted(@Query("who_requested") String whoRequested,@Query("who_accepts") String whoAccepts);

	/**
	 * Sends a request to server in order to decline a friend
	 * @param whoRequested the user that get's accepted as a friend
	 * @param whoDeclines  the user that accepts the friend
	 * @return
	 */
	@POST(ServerConstants.FRIENDS_REJECTED)
	Observable<JsonObject> friendRejected(@Query("who_requested") String whoRequested,@Query("who_accepts") String whoDeclines);
}
