package com.marked.pixsee.data.friends;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.ui.friendsInvite.addUsername.Relationship;

import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import static com.marked.pixsee.networking.ServerConstants.FRIENDS;
import static com.marked.pixsee.networking.ServerConstants.FRIENDS_ACCEPTED;
import static com.marked.pixsee.networking.ServerConstants.FRIENDS_REJECTED;
import static com.marked.pixsee.networking.ServerConstants.FRIENDS_REQUEST;
import static com.marked.pixsee.networking.ServerConstants.SEARCH;
import static com.marked.pixsee.networking.ServerConstants.USER;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public interface FriendsAPI {
	String SERVER_USER_FRIENDS = USER + "/{id}/" + FRIENDS;
	String USER_SEARCH = USER + "/{id}/" + SEARCH;
	String USER_UNFRIEND = USER + "/{unfriends}/" + FRIENDS;

	@GET(SERVER_USER_FRIENDS)
	Observable<JsonArray> getUsers(@Path("id") String userId);

	/**
	 * Sends a request to server in order to accept a friend
	 *
	 * @param whoRequested the user that get's accepted as a friend
	 * @param whoAccepted   the user that accepts the friend
	 * @return
	 */
	@Multipart
	@PUT(FRIENDS_ACCEPTED)
	Observable<JsonObject> friendAccepted(@Part("whoRequested") String whoRequested, @Part("whoAccepted") String whoAccepted);

	/**
	 * Sends a request to server in order to decline a friend
	 *
	 * @param whoRequested the user that get's accepted as a friend
	 * @param whoDeclines  the user that accepts the friend
	 * @return
	 */
	@Multipart
	@PUT(FRIENDS_REJECTED)
	Observable<JsonObject> friendRejected(@Part("whoRequested") String whoRequested, @Part("whoAccepted") String whoDeclines);

	@GET(USER_SEARCH)
	Observable<JsonArray> searchUsersByUsername(@Path("id") String id, @Query("username") String username);

	@Multipart
	@POST(FRIENDS_REQUEST)
	Observable<Relationship> friendRequest(@Part("sender") User sender, @Part("receiver") User receiver);

	@PUT(USER_UNFRIEND)
	Observable<Relationship> unfriend(@Path("unfriends") String unfriends,
	                                  @Query("unfriend") String unfriend);
}
