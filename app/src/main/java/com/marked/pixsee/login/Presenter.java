package com.marked.pixsee.login;

import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.data.repository.user.UserRepository;
import com.marked.pixsee.utility.GCMConstants;

import java.lang.ref.WeakReference;

import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 14-Jun-16.
 */
public class Presenter implements LoginContract.Presenter {
	private final WeakReference<LoginContract.View> mView;
	private final Retrofit mRetrofit;
	private final UserRepository mRepository;
	private SharedPreferences mPreferences;
	private String mToken;

	public Presenter(LoginContract.View view, Retrofit retrofit, UserRepository repository, SharedPreferences preferences) {
		mView = new WeakReference<LoginContract.View>(view);
		mRetrofit = retrofit;
		mRepository = repository;
		mPreferences = preferences;
		mView.get().setPresenter(this);
	}

	@Override
	public void start() {
		mToken = FirebaseInstanceId.getInstance().getToken();
	}

	/**
	 * Handle action Login in the provided background thread with the provided
	 * parameters.
	 * Persist registration to third-party servers.
	 * Modify this method to associate the user's GCM registration token with any server-side account
	 * maintained by your application. If the user already has an account, update his registration token in case it's outdated
	 *
	 * @param email    The email of the account.
	 *                 *
	 * @param password The password to login
	 *                 *
	 */
	@Override
	public void handleLogin(String email, String password) {
		mRetrofit.create(LoginAPI.class)
				.login(email, password, mToken)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Response<JsonObject>>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(Response<JsonObject> response) {
						if (response.isSuccess()) {
							Gson gson = new Gson();
							User user = gson.fromJson(response.body().get("user").getAsJsonObject(), User.class);
							mRepository.saveAppUser(user);
							/* You should store a boolean that indicates whether the generated token has been
					         sent to your server. If the boolean is false, send the token to your server,
                             otherwise your server should have already received the token.
                             */
							JsonArray friends;
							if (response.body().get(GCMConstants.FRIENDS).getAsJsonArray() == null)
								friends = new JsonArray();
							else
								friends = response.body().get(GCMConstants.FRIENDS).getAsJsonArray();
							mPreferences.edit()
									.putBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, true)
									.apply();/* if sent_token_to_server == true, we are registered*/
							mPreferences.edit()
									.putString(GCMConstants.USER_ID, user.getUserID())
									.apply();
						} else {

						}
					}
				});
	}
}
