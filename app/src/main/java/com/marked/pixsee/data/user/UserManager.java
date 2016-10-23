package com.marked.pixsee.data.user;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.marked.pixsee.utility.GCMConstants;

import javax.inject.Inject;

/**
 * Created by Tudor on 23-Oct-16.
 */

public class UserManager {
	private User appUser;
	private SharedPreferences mSharedPreferences;
	private Gson mGson;

	@Inject
	public UserManager(SharedPreferences sharedPreferences, Gson gson) {
		mSharedPreferences = sharedPreferences;
		mGson = gson;
	}

	public void saveUser(User user) {
		appUser = user;
		String appUser = mGson.toJson(user);
		mSharedPreferences.edit().putString(GCMConstants.APP_USER, appUser).apply();
	}

	public User loadUser() {
		String user = mSharedPreferences.getString(GCMConstants.APP_USER, "{}");
		appUser = mGson.fromJson(user, User.class);
		return appUser;
	}

	public User getAppUser() {
		if (appUser == null)
			return loadUser();
		return appUser;
	}
}
