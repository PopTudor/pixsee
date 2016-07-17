package com.marked.pixsee.networking;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Tudor on 26-Jun-16.
 */
public interface UploadAPI {
	@Multipart
	@POST(ServerConstants.SERVER_UPLOAD)
	Observable<Response<JsonObject>> upload(@Part("from")String from,@Part("to") String to, @Part MultipartBody.Part file);

	@GET(ServerConstants.SERVER_USER_IMAGE)
	Observable<Response<byte[]>> download(@Query("pictureName") String pictureName);
}
