package com.marked.vifo.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor Pop on 12-Dec-15.
 * Singleton class used to keep all the friends (the list of contacts) of the user
 */
public class Contacts {
	private static Contacts ourInstance;
	private static Context mContext;
	private List<Contact> mContacts;

	private Contacts(Context context) {
		mContext = context;
		mContacts = new ArrayList<>();
	}

	public static Contacts getInstance(Context context) {
		if (ourInstance == null)
			ourInstance = new Contacts(context);
		return ourInstance;
	}

	public static List<Contact> fromJSONArray(JSONArray jsonArray) {
		List<Contact> contacts = new ArrayList<>();

		JSONObject object;
		String id;
		String firstName;
		String lastName;
		String token;
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				object = jsonArray.getJSONObject(i);
				id = object.getString(Contact.ID);
				firstName = object.getString(Contact.FIRST_NAME);
				lastName = object.getString(Contact.LAST_NAME);
				token = object.getString(Contact.TOKEN);

				contacts.add(new Contact(id, firstName, lastName, firstName + " " + lastName, token));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return contacts;
	}

	public static JSONArray toJSONArray(List<Contact> list) {
		JSONArray jsonArray = new JSONArray();
		for (Contact contact : list)
			jsonArray.put(contact.toJSON());
		return jsonArray;
	}

	public List<Contact> getContacts() {
		return mContacts;
	}

	public void setFriends(List<Contact> contacts) {
		mContacts.clear();
		addContacts(contacts);
	}

	public boolean addContact(Contact contact) {
		mContacts.add(contact);
		return true;
	}

	public boolean addContacts(List<Contact> contacts) {
		mContacts.addAll(contacts);
		return true;
	}

	public boolean removeContact(Contact contact) {
		return mContacts.remove(contact);
	}

	public Contact removeContact(int position) {
		if (!mContacts.isEmpty())
			return mContacts.remove(position);
		else
			return null;
	}

}
