package com.marked.pixsee.signup;

import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.data.repository.user.UserDatasource;
import com.marked.pixsee.login.LoginAPI;
import com.marked.pixsee.networking.HTTPStatusCodes;
import com.marked.pixsee.networking.ServerConstants;
import com.marked.pixsee.utility.GCMConstants;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import javax.inject.Named;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 13-Jun-16.
 */
public class Presenter implements SignUpContract.Presenter {
	private WeakReference<SignUpContract.View> mView;
	private Retrofit mRetrofit;
	private String mName;
	private String mEmail;
	private String mPassword;
	private String mUsername;
	private UserDatasource mDatasource;
	private SharedPreferences mPreferences;

	public Presenter(SignUpContract.View view, @Named(ServerConstants.SERVER) Retrofit retrofit, UserDatasource datasource, SharedPreferences preferences) {
		mDatasource = datasource;
		mPreferences = preferences;
		mView = new WeakReference<SignUpContract.View>(view);
		mRetrofit = retrofit;
		mView.get().setPresenter(this);
	}

	@Override
	public void start() {
		mView.get().showSignupStepName();
	}

	/**
	 * Sends a request to the server to check if the email is already in the database.
	 * If the email is registered we notify him. Else we show the next step.
	 * Else proceed to the next step
	 *
	 * @param email the email adress to verify on the server
	 */
	@Override
	public void checkEmail(String email) {
		mRetrofit.create(LoginAPI.class)
				.hasAccount(email)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Response<Void>>() {
					@Override
					public void onCompleted() {
						mView.get().hideDialog();
					}

					@Override
					public void onError(Throwable e) {
						if (e instanceof HttpException && ((HttpException) e).code() == HTTPStatusCodes.REQUEST_OK) { // if user is not found on the
							// server, go to
							// next step
							mView.get().showSignupStepPassword();
						}
						if (e instanceof SocketTimeoutException)
							mView.get().showToast("Timeout Error");
					}

					@Override
					public void onNext(Response<Void> jsonObject) {
						if (jsonObject != null && jsonObject.isSuccess() && jsonObject.code() == HTTPStatusCodes.REQUEST_CONFLICT)
							mView.get().showToast("You already have an account");
						else if (jsonObject != null && jsonObject.isSuccess() && jsonObject.code() == HTTPStatusCodes.REQUEST_OK) {
							mView.get().showSignupStepPassword();
						}
					}
				});
	}

	@Override
	public void onSaveUsername(final String username) {
		mUsername = username;
		mView.get().showDialog("Signing up", "Please wait...");
		mRetrofit.create(LoginAPI.class)
				.checkUsername(username)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Response<Void>>() {
					@Override
					public void onCompleted() {
						mView.get().hideDialog();
						mView.get().signupComplete(mName, mEmail, mUsername, mPassword);
					}

					@Override
					public void onError(Throwable e) {
						mView.get().hideDialog();
					}

					@Override
					public void onNext(Response<Void> voidResponse) {
						if (voidResponse.isSuccess() && voidResponse.code() == HTTPStatusCodes.REQUEST_OK) {
							handleActionSignup(mName, mEmail,mUsername, mPassword, FirebaseInstanceId.getInstance().getToken());
						} else if (voidResponse.code() == HTTPStatusCodes.REQUEST_CONFLICT) {
							mView.get().showToast("The username is already taken.");
						}
					}
				});
	}

	@Override
	public void onSaveName(String name) {
		mName = name;
		mView.get().showSignupStepEmail(name);
	}

	@Override
	public void onSaveEmail(String emailEditText) {
		checkEmail(emailEditText);
		mView.get().showDialog("", "");
		mEmail = emailEditText;
	}

	@Override
	public void onSavePassword(String password) {
		mPassword = password;
	}

	/**
	 * Handle action Signup in the provided background thread with the provided
	 * parameters.
	 * Persist registration to third-party servers.
	 * Modify this method to associate the user's GCM registration token with any server-side account
	 * maintained by your application.
	 *
	 * @param email    The email of the user we want to store.
	 * @param password The password of the user.
	 * @param token    The new token.
	 */
	public void handleActionSignup(String name, String email, String username,String password, String token) {
		mRetrofit.create(LoginAPI.class)
				.create(name, email, username,password,token)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Response<JsonObject>>() {
					@Override
					public void onCompleted() {
						mView.get().hideDialog();
					}

					@Override
					public void onError(Throwable e) {
						mView.get().hideDialog();
					}

					@Override
					public void onNext(Response<JsonObject> response) {
						if (response.isSuccess()) {
							Gson gson = new Gson();
							User user = gson.fromJson(response.body().get("user").getAsJsonObject(), User.class);
							mDatasource.saveAppUser(user);

							// You should store a boolean that indicates whether the generated token has been
							// sent to your server. If the boolean is false, send the token to your server,
							// otherwise your server should have already received the token.
							mPreferences.edit().putBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, true).apply();/* if sent_token_to_server == true, we are registered*/
							mPreferences.edit().putString(GCMConstants.USER_ID, user.getUserID()).apply();
							mView.get().showMainScreen();
						} else {
							mPreferences.edit().putBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, false).apply();
						}
					}
				});
	}
}
