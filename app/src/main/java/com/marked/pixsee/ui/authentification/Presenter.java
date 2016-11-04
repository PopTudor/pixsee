package com.marked.pixsee.ui.authentification;

import android.util.Patterns;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserManager;
import com.marked.pixsee.networking.HTTPStatusCodes;
import com.marked.pixsee.ui.authentification.login.LoginAPI;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 13-Jun-16.
 */
public class Presenter implements AuthenticationContract.Presenter {
	private final WeakReference<AuthenticationContract.View> mView;
	private final Gson mGson;
	private final LoginAPI mLoginAPI;
	private final UserManager mManager;
	private String mName;
	private String mEmail;
	private String mPassword;
	private String mUsername;

	public Presenter(AuthenticationContract.View view, LoginAPI loginAPI, UserManager manager, Gson gson) {
		mLoginAPI = loginAPI;
		mManager = manager;
		mView = new WeakReference<AuthenticationContract.View>(view);
		mGson = gson;
		mView.get().setPresenter(this);
	}

	@Override
	public void attach() {
		mView.get().showLoginStep();
	}

	@Override
	public void detach() {

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
		mLoginAPI.hasAccount(email)
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
						if (jsonObject.code() == HTTPStatusCodes.REQUEST_CONFLICT)
							mView.get().showToast("You already have an account");
						else if (jsonObject.isSuccessful() && jsonObject.code() == HTTPStatusCodes.REQUEST_OK) {
							mView.get().showSignupStepPassword();
						}
					}
				});
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
		if (validateLogin(email, password)) {
			mLoginAPI.login(email, password, FirebaseInstanceId.getInstance().getToken())
					.filter(new Func1<Response<JsonObject>, Boolean>() {
						@Override
						public Boolean call(Response<JsonObject> jsonObjectResponse) {
							return jsonObjectResponse != null;
						}
					})
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.doOnSubscribe(new Action0() {
						@Override
						public void call() {
							mView.get().showDialog("Login","Please wait...");
						}
					})
					.subscribe(new Subscriber<Response<JsonObject>>() {
						@Override
						public void onCompleted() {
							mView.get().hideDialog();
						}

						@Override
						public void onError(Throwable e) {
							e.printStackTrace();
							if (e instanceof SocketTimeoutException){
								mView.get().showToast("Server is not responding. Please try again later !");
							}else if (e instanceof ConnectException)
								mView.get().showToast("Failed to connect...");
							mView.get().hideDialog();
						}

						@Override
						public void onNext(Response<JsonObject> response) {
							if (response.isSuccessful() && response.code() == HTTPStatusCodes.REQUEST_OK) {
								User user = mGson.fromJson(response.body().getAsJsonObject(), User.class);
								mManager.saveUser(user);
								mView.get().showMainScreen();
							} else
								mView.get().showToast("Invalid Credentials");
						}
					});
		}
	}

	private boolean validateLogin(String email, String password) {
		if (email.isEmpty()) {
			mView.get().showToast("The email field is empty");
			return false;
		} else if (password.isEmpty()) {
			mView.get().showToast("The password field is empty");
			return false;
		} else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
			mView.get().showToast("You must enter a valid email");
			return false;
		}
		return true;
	}

	@Override
	public void onSaveUsername(final String username) {
		mUsername = username;
		mView.get().showDialog("Signing up", "Please wait...");
		mLoginAPI.checkUsername(username)
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
						if (voidResponse.isSuccessful() && voidResponse.code() == HTTPStatusCodes.REQUEST_OK) {
							handleActionSignup(mName, mEmail, mUsername, mPassword, FirebaseInstanceId.getInstance().getToken());
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

	public void handleActionSignup(String name, String email, String username, String password, String token) {
		mLoginAPI.create(name, email, username, password, token)
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
						if (response.isSuccessful()) {
							User user = mGson.fromJson(response.body().get("user").getAsJsonObject(), User.class);
							mManager.saveUser(user);
							mView.get().showMainScreen();
						}
					}
				});
	}
}
