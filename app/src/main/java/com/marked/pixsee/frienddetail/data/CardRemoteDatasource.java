package com.marked.pixsee.frienddetail.data;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.utility.GCMConstants;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 2016-05-20.
 */
public class CardRemoteDatasource implements CardDatasource {
	private final Retrofit retrofit;
	private final String userid;
	private final Gson gson = new Gson();

	public CardRemoteDatasource(SharedPreferences sharedPreferences) {
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient httpClient = new OkHttpClient.Builder()
				                          .addInterceptor(loggingInterceptor)
				                          .build();
		retrofit = new Retrofit.Builder()
				           .baseUrl(ServerConstants.SERVER)
				           .addConverterFactory(GsonConverterFactory.create())
				           .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				           .client(httpClient)
				           .build();

		userid = sharedPreferences.getString(GCMConstants.USER_ID, "");
	}

	@Override
	public Observable<List<Message>> getMessagesOfFriend(String friendId) {
		return retrofit.create(MessageAPI.class)
				       .getMessagesOfFriend(userid, friendId)
				       .subscribeOn(Schedulers.io())
				       .map(new Func1<JsonObject, JsonArray>() {
					       @Override
					       public JsonArray call(JsonObject jsonObject) {
						       return jsonObject.getAsJsonArray(DatabaseContract.Message.TABLE_NAME);
					       }
				       })
				       .map(new Func1<JsonArray, List<Message>>() {
					       @Override
					       public List<Message> call(JsonArray jsonElements) {
						       final List<Message> cache = new ArrayList<>();
						       for (JsonElement it : jsonElements){
							       cache.add(gson.fromJson(it.toString(), Message.class));
						       }
						       return cache;
					       }
				       });
	}

	@Override
	public Observable<Message> getMessage(@NonNull Message MessageId) {
		return null;
	}

	@Override
	public void saveMessage(@NonNull Message Message) {

	}

	@Override
	public void updateMessage(@NonNull Message message) {

	}

	@Override
	public void saveMessage(@NonNull List<Message> Message) {

	}

	@Override
	public void refreshMessages() {

	}

	@Override
	public void deleteAllMessages() {

	}

	@Override
	public void deleteMessages(@NonNull Message MessageId) {

	}
}
