package com.marked.pixsee.data.user;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tudor Pop on 28-Nov-15.
 */
public final class User implements Parcelable, Comparable<User> {
	public static final String TAG = "user_tag";
	@SerializedName(value = "userID", alternate = {"_id"})
	String userID;
	String name;
	String email;
	String token;
	String password;
	String coverUrl;
	String username;
	String iconUrl;

	public User(String userID, String name, String email, String token, String password, String coverUrl, String iconUrl,  String username) {
		this.userID = userID;
		this.name = name;
		this.email = email;
		this.token = token;
		this.password = password;
		this.coverUrl = coverUrl;
		this.iconUrl = iconUrl;
		this.username = username;
	}

	public User(String userID, String name, String email, String token, String password, String coverUrl, String iconUrl) {
		this(userID, name, email, token, password, coverUrl, iconUrl, null);
	}

	public User(String userID, String name, String email, String token, String password, String coverUrl) {
		this(userID, name, email, token, password, coverUrl, null);
	}

	public User(String userID, String name, String email, String token, String password) {
		this(userID, name, email, token, password, null);
	}

	public User(String userID, String name, String email, String token) {
		this(userID, name, email, token, null, null, null, null);
	}

	public User(Parcel parcelIn) {
		this.userID = parcelIn.readString();
		this.name = parcelIn.readString();
		this.email = parcelIn.readString();
		this.token = parcelIn.readString();
		this.password = parcelIn.readString();
		this.coverUrl = parcelIn.readString();
		this.iconUrl = parcelIn.readString();
		this.username = parcelIn.readString();
	}


	public static final Parcelable.Creator CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel parcelIn) {
			return new User(parcelIn);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	public String getUserID() {
		return userID;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userID);
		dest.writeString(name);
		dest.writeString(email);
		dest.writeString(token);
		dest.writeString(password);
		dest.writeString(coverUrl);
		dest.writeString(iconUrl);
		dest.writeString(username);
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getToken() {
		return token;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	@Override
	public int compareTo(@NonNull User another) {
		return this.email.compareTo(another.email);
	}

	public static List<User> getRandomUsers(int num) {
		String[] names = {"Vincenza Goudeau", "Kellee Petrillo", "Nga Kinchen", "Leif Vara", "Bradley Mcgonigle", "Kasi Kitchen"};
		List<User> users = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			String name = names[(int) (Math.random() * names.length)];
			String email = name.replace(" ", "")+"@gmail.com";
			users.add(new User(UUID.randomUUID().toString(), name, email, name + email, null, null, null, name));
		}
		return users;
	}
}
