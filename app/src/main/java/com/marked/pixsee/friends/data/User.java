package com.marked.pixsee.friends.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Tudor Pop on 28-Nov-15.
 */
public final class User implements Parcelable {
	@SerializedName(value = "userID", alternate = {"_id"})
	String userID;
	String name;
	String email;
	String token;
	String password;
	String cover;

	String icon;

	public User(String userID, String name, String email, String token, String password, String cover, String icon) {
		this.userID = userID;
		this.name = name;
		this.email = email;
		this.token = token;
		this.password = password;
		this.cover = cover;
		this.icon = icon;
	}


	public User(String userID, String name, String email, String token) {
		this.userID = userID;
		this.name = name;
		this.email = email;
		this.token = token;
	}

	public User(Parcel parcelIn) {
		this.userID = parcelIn.readString();
		this.name = parcelIn.readString();
		this.email = parcelIn.readString();
		this.token = parcelIn.readString();
		this.password = parcelIn.readString();
		this.cover = parcelIn.readString();
		this.icon = parcelIn.readString();
	}

	public String getCover() {
		return cover;
	}

	public String getIcon() {
		return icon;
	}

	public User(String userID, String name, String email, String token, String password) {
		this(userID, name, email, token);
		this.password = password;
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
		dest.writeString(cover);
		dest.writeString(icon);
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
}
