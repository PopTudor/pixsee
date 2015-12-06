package com.marked.vifo.controller;

import android.content.Context;

import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by Tudor Pop on 01-Dec-15.
 */
public class RequestQueue {
    private Context mContext;
    private static RequestQueue ourInstance;
    private com.android.volley.RequestQueue mQueue;

    public static RequestQueue getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new RequestQueue(context);
        return ourInstance;
    }

    private RequestQueue(Context context) {
        mContext = context;
        mQueue = Volley.newRequestQueue(mContext);
    }

    public void add(JsonRequest request) {
        mQueue.add(request);
    }
}
