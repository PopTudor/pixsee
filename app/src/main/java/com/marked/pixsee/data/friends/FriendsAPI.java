package com.marked.pixsee.data.friends;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.marked.pixsee.networking.ServerConstants;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import static com.marked.pixsee.networking.ServerConstants.FRIENDS_ACCEPTED;
import static com.marked.pixsee.networking.ServerConstants.FRIENDS_REJECTED;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public interface FriendsAPI {
	String SERVER_USER_FRIENDS = ServerConstants.USER + "/{id}/" + ServerConstants.FRIENDS;

	@GET(SERVER_USER_FRIENDS)
	Observable<JsonArray> getUsers(@Path("id") String userId);

	/**
	 * Sends a request to server in order to accept a friend
	 * @param whoRequested the user that get's accepted as a friend
	 * @param whoAccepts  the user that accepts the friend
	 * @return
	 */
	@POST(FRIENDS_ACCEPTED)
	Observable<JsonObject> friendAccepted(@Query("who_requested") String whoRequested,@Query("who_accepts") String whoAccepts);

	/**
	 * Sends a request to server in order to decline a friend
	 * @param whoRequested the user that get's accepted as a friend
	 * @param whoDeclines  the user that accepts the friend
	 * @return
	 */
	@POST(FRIENDS_REJECTED)
	Observable<JsonObject> friendRejected(@Query("who_requested") String whoRequested,@Query("who_accepts") String whoDeclines);
}
