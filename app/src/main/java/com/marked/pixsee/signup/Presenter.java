package com.marked.pixsee.signup;

import com.marked.pixsee.login.LoginAPI;
import com.marked.pixsee.networking.ServerConstants;

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
public class Presenter implements SignupContract.Presenter {
	private WeakReference<SignupContract.View> mView;
	private Retrofit mRetrofit;

	public Presenter(SignupContract.View view,@Named(ServerConstants.SERVER) Retrofit retrofit) {
		mView = new WeakReference<SignupContract.View>(view);
		mRetrofit = retrofit;
		mView.get().setPresenter(this);
	}

	@Override
	public void start() {

	}
	/**
	 * Sends a request to the server to check if the email is already in the database.
	 * If that is true the user already has an account and we should tell him that
	 * Else proceed to the next step
	 *
	 * @param email the email adress to verify on the server
	 */
	@Override
	public void checkEmail(String email) {
		LoginAPI service = mRetrofit.create(LoginAPI.class);
		service.hasAccount(email)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Response<Void>>() {
					@Override
					public void onCompleted() {
						mView.get().hideDialog();
					}

					@Override
					public void onError(Throwable e) {
						if (e instanceof HttpException && ((HttpException) e).code()==404){
							mView.get().showSignUpPasswordStep();
						}
						if (e instanceof SocketTimeoutException)
							mView.get().showToast("Timeout Error");
					}

					@Override
					public void onNext(Response<Void> jsonObject) {
						if (jsonObject != null && jsonObject.isSuccess())
							mView.get().showToast("You already have an account");
					}
				});
	}
}
