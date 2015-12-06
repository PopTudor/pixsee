package com.marked.vifo.model;

import android.content.Context;

import com.marked.vifo.controller.RequestQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor Pop on 01-Dec-15.
 */
public class Contacts {
    private static Contacts ourInstance;
    private Context mContext;
    private List<Contact> mContacts;
    private RequestQueue mQueue;

    public static Contacts getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new Contacts(context);
        return ourInstance;
    }

    private Contacts(Context context) {
        mContext = context;
        mContacts = new ArrayList<>();
        mQueue = RequestQueue.getInstance(context);
    }

    public List<Contact> getContacts() {
        return mContacts;
    }

    public void setContacts(List<Contact> contacts) {
        mContacts = contacts;
    }

    public void addContact(Contact contact){
        mContacts.add(contact);
    }

}
