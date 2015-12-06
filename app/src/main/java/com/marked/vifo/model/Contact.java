package com.marked.vifo.model;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Tudor Pop on 28-Nov-15.
 */
public class Contact implements Comparator<Contact>, Comparable<Contact> {
	private String name;
	private String token;
	private String _id; /*_id from mongodb*/
	private List<Contact> friends;

	public Contact(String name, String token, String _id) {
		this(name, token, _id,null);
	}

	public Contact(String name, String token, String id, List<Contact> friends) {
		this.name = name;
		this.token = token;
		this._id = id;
		this.friends = friends;
	}



	public String getName() {
		return name;
	}

	public String getToken() {
		return token;
	}

	public String getId() {
		return _id;
	}

	@Override
	public int compareTo(Contact another) {
		return _id.compareTo(another._id);
	}

	@Override
	public int compare(Contact lhs, Contact rhs) {
		return lhs._id.compareTo(rhs._id);
	}

	@Override
	public boolean equals(Object o) {
		Contact contact = (Contact) o;
		return _id.equals(contact._id);
	}


}
