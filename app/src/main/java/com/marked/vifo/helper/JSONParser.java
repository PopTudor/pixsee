package com.marked.vifo.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

import static com.android.volley.Request.Method.POST;
import static com.android.volley.Response.ErrorListener;

/**
 * Created by Tudor Pop on 08-Nov-15.
 */
public class JSONParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static JSONArray jAry = null;
    static String json = "";
    private static RequestQueue queue;
    // constructor
    public JSONParser(Context context) {
        queue = Volley.newRequestQueue(context);
    }
    public JSONObject getJSONFromUrl(String url) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(POST, url, new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        jObj = response;
                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("***", "onErrorResponse " +" JSON object could not be downloaded");
                    }
                });

        queue.add(jsObjRequest);
        // return JSON String
        return jObj;
    }

    public JSONArray getJSONArray(String url) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(POST, url, new Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        jAry = response;
                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("***", "onErrorResponse " +" JSON object could not be downloaded");
                    }
                });

        queue.add(jsObjRequest);
        return jAry;
    }

}
