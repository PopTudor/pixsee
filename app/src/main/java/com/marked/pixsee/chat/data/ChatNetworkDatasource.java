package com.marked.pixsee.chat.data;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.marked.pixsee.model.user.User;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.utility.GCMConstants;

import java.util.List;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Tudor on 2016-05-20.
 */
public class ChatNetworkDatasource implements ChatDatasource {
	private final Retrofit retrofit;
	private final String userid;
	private final Gson gson = new Gson();

	/*
	* https://frogermcs.github.io/dependency-injection-with-dagger-2-the-api/
	* All parameters are taken from dependencies graph. @Inject annotation used in costructor
	* also makes this class a part of dependencies graph.
	* It means that it can also be injected when it’s needed:
	* @Inject ChatNetworkDatasource networkData;
	* and it can also be provided to methods that needs this dependency:
	* @provides
	* public Repository provideRepository(ChatNetworkDatasource source,ChatLocalds local){
	*   return new Repository(source,local);
	* }
	* but it cannot be provided for an interface(unless annotated with qualified)
	* the following won't work:
	* @provides
	* public Repository provideRepository(ChatDatasource source){
	*   return new Repository(source);
	* }
	* */
	@Inject
	public ChatNetworkDatasource(SharedPreferences sharedPreferences) {
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

    /**
     * //todo not needed now since we don't want to store messages on the server
     * Get messages for a given {@link User} from the server
     * Messages represent the conversation between this user and it's friend
     * @param friend who's messages to get
     * @return return messages
     */
    @Override
	public Observable<List<Message>> getMessages(User friend) {
//		return retrofit.create(MessageAPI.class)
//				       .getMessagesOfFriend(userid, friendId.getUserID())
//				       .subscribeOn(Schedulers.io())
//				       .map(new Func1<JsonObject, JsonArray>() {
//					       @Override
//					       public JsonArray call(JsonObject jsonObject) {
//						       return jsonObject.getAsJsonArray(DatabaseContract.Message.TABLE_NAME);
//					       }
//				       })
//				       .map(new Func1<JsonArray, List<Message>>() {
//					       @Override
//					       public List<Message> call(JsonArray jsonElements) {
//						       final List<Message> cache = new ArrayList<>();
//						       for (JsonElement it : jsonElements){
//							       cache.add(gson.fromJson(it.toString(), Message.class));
//						       }
//						       return cache;
//					       }
//				       });
		return Observable.empty();
	}

	@Override
	public Observable<Message> getMessage(@NonNull Message MessageId) {
		return Observable.empty();
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

	/**
	 * get all messages from the server
	 */
	@Override
	public void refreshMessages() {

	}

	/**
	 * delete all messages
	 */
	@Override
	public void deleteAllMessages() {
		// TODO: 6/1/2016 we don't need to delete messages on the server since we don't store any
	}

	/**
	 * @param MessageId
	 */
	@Override
	public void deleteMessages(@NonNull Message MessageId) {
		// TODO: 6/1/2016 we don't need to delete messages on the server since we don't store any
	}
}
