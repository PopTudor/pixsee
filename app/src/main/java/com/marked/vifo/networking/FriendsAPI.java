package com.marked.vifo.networking;

import com.marked.vifo.extra.ServerConstants;

import org.json.JSONObject;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Tudor Pop on 17-Feb-16.
 */
public interface FriendsAPI {
	@GET(ServerConstants.USER + "/" + ServerConstants.FRIENDS)
	Observable<JSONObject> listFriends(@Query("id") String id);
}
