package com.marked.pixsee.data;

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
	}
}
