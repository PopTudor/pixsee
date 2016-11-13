package com.marked.pixsee.data.user;

import android.os.Bundle;
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
public class User implements Parcelable, Comparable<User> {
	public static final String TAG = "user_tag";
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
	@SerializedName(value = "userId", alternate = {"id"})
	String id;
	String name;
	String email;
	String pushToken;
	String password;
	String username;
	String phone;

	public User(String id, String name, String email, String pushToken, String password, String username) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.pushToken = pushToken;
		this.password = password;
		this.username = username;
	}

	public User() {
	}

	public User(String email, String username, String password, String pushToken) {
		this.email = email;
		this.pushToken = pushToken;
		this.password = password;
		this.username = username;
	}

	public User(Parcel parcelIn) {
		this.id = parcelIn.readString();
		this.name = parcelIn.readString();
		this.email = parcelIn.readString();
		this.pushToken = parcelIn.readString();
		this.password = parcelIn.readString();
		this.username = parcelIn.readString();
	}

	public User(Bundle extras) {
		this(extras.getString("id"), extras.getString("name"), extras.getString("email"), extras.getString("pushToken")
				, null, extras.getString("username"));
	}

	public static List<User> getRandomUsers(int num) {
		String[] names = {"Vincenza Goudeau", "Kellee Petrillo", "Nga Kinchen", "Leif Vara", "Bradley Mcgonigle", "Kasi Kitchen"};
		List<User> users = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			String name = names[(int) (Math.random() * names.length)];
			String email = name.replace(" ", "") + "@gmail.com";
			users.add(new User(UUID.randomUUID().toString(), name, email, name + email, null, name));
		}
		return users;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(email);
		dest.writeString(pushToken);
		dest.writeString(password);
		dest.writeString(username);
	}

	public String getPushToken() {
		return pushToken;
	}

	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public int compareTo(@NonNull User another) {
		return this.email.compareTo(another.email);
	}
}
