package com.marked.vifo.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

/**
 * Created by Tudor Pop on 28-Nov-15.
 */
public class Contact implements Comparator<Contact>, Comparable<Contact>,Parcelable {
	public static final String ID = "_id";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String TOKEN = "token";

	private String mFirstName;
	private String mLastName;
	private String mName;
	private String mToken;
	private String _id; /*_id from mongodb*/

	public Contact(String id, String firstName, String lastName, String name, String token) {
		_id = id;
		mFirstName = firstName;
		mLastName = lastName;
		mName = name;
		mToken = token;
	}

	public Contact(String id, String firstName, String lastName, String token) {
		this(id, firstName, lastName, null, token);
		setName(firstName, lastName);
	}

	public Contact() {
	}

	public Contact(String id, String token) {
		this(id, null, token);
		mToken = token;
	}

	public Contact(String firstName, String lastName, String token) {
		mFirstName = firstName;
		mLastName = lastName;
		mToken = token;
		setName(firstName, lastName);
	}

	protected Contact(Parcel in) {
		mFirstName = in.readString();
		mLastName = in.readString();
		mName = in.readString();
		mToken = in.readString();
		_id = in.readString();
	}

	public static final Creator<Contact> CREATOR = new Creator<Contact>() {
		@Override
		public Contact createFromParcel(Parcel in) {
			return new Contact(in);
		}

		@Override
		public Contact[] newArray(int size) {
			return new Contact[size];
		}
	};

	public void setFirstName(String firstName) {
		this.mFirstName = firstName;
	}

	public void setLastName(String lname) {
		this.mLastName = lname;
	}

	public String getName() {
		if (mName==null || mName.isEmpty())
			setName(mFirstName, mLastName);
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public void setName(String firstName, String lastName) {
		this.mName = String.format("%s %s", firstName, lastName).trim();
	}

	public String getToken() {
		return mToken;
	}

	public void setToken(String token) {
		this.mToken = token;
	}

	public String getId() {
		return _id;
	}

	public void setId(String id) {
		this._id = id;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public String getLastName() {
		return mLastName;
	}

	@Override
	public int compareTo(@NonNull Contact another) {
		return _id.compareTo(another._id);
	}

	@Override
	public int compare(Contact lhs, Contact rhs) {
		return lhs._id.compareTo(rhs._id);
	}

	@Override
	public String toString() {
		return "{_id:" + _id + ",firstName:" + mFirstName + ",lastName:" + mLastName + ",token:" + mToken + "}";
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Contact && _id.equals(((Contact) o)._id);
	}

	public JSONObject toJSON(){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(ID, _id);
			jsonObject.put(FIRST_NAME, mFirstName == null ? "null" : mFirstName);
			jsonObject.put(LAST_NAME, mLastName == null ? "null" : mLastName);
			jsonObject.put(TOKEN, mToken);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	public void fromJSON(JSONObject object){
		try {
			_id = object.getString(ID);
			mFirstName = object.getString(FIRST_NAME);
			mLastName = object.getString(LAST_NAME);
			mToken = object.getString(TOKEN);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mFirstName);
		dest.writeString(mLastName);
		dest.writeString(mName);
		dest.writeString(mToken);
		dest.writeString(_id);
	}
}
