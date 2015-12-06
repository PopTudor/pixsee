package com.marked.vifo.activity;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.marked.vifo.R;
import com.marked.vifo.controller.RequestQueue;
import com.marked.vifo.extra.HTTPStatusCodes;
import com.marked.vifo.fragment.SignUpEmailFragment;
import com.marked.vifo.fragment.SignUpNameFragment;
import com.marked.vifo.fragment.SignUpPassFragment;
import com.marked.vifo.gcm.RegistrationBroadcastReceiver;
import com.marked.vifo.extra.GCMConstants;
import com.marked.vifo.gcm.service.LogInRegistrationIntentService;
import com.marked.vifo.helper.Utils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SignUpActivity extends AppCompatActivity implements SignUpNameFragment.SignUpNameFragmentInteraction, SignUpEmailFragment.SignUpEmailFragmentInteraction, SignUpPassFragment.SignUpPassFragmentInteraction {
    private FragmentManager mFragmentManager;
    private RegistrationBroadcastReceiver mRegistrationBroadcastReceiver;
    private CoordinatorLayout mContainer;
    private LocalBroadcastManager mBroadcastManagerastManager;
    private String mName;
    private String mEmail;
    private String mPassword;
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mContainer = (CoordinatorLayout) findViewById(R.id.fragmentContainer);
        mProgressDialog = new ProgressDialog(this);

        mFragmentManager = getSupportFragmentManager();
        mBroadcastManagerastManager = LocalBroadcastManager.getInstance(this);
        mRequestQueue = RequestQueue.getInstance(this);
        // GCM registration
        mRegistrationBroadcastReceiver = new RegistrationBroadcastReceiver();
        mRegistrationBroadcastReceiver.setOnRegistrationBroadcastReceiverListener(new RegistrationBroadcastReceiver.RegistrationBroadcastReceiverListener() {
            @Override
            public void onDismiss() {
                mProgressDialog.dismiss();
            }

            @Override
            public void onError(int errorStatusCode) {
                mProgressDialog.dismiss();
                Snackbar snackbar = null;
                switch (errorStatusCode) {
                    case HTTPStatusCodes.REQUEST_CONFLICT:
                        snackbar = Utils.createWhiteSnackBar(SignUpActivity.this, mContainer, "You already have an account");
                        break;
                    case HTTPStatusCodes.REQUEST_TIMEOUT:
                        snackbar = Utils.createWhiteSnackBar(SignUpActivity.this, mContainer, "Timeout error");
                        break;
                    case HTTPStatusCodes.REQUEST_INCORRECT_PASSWORD:
                        snackbar = Utils.createWhiteSnackBar(SignUpActivity.this, mContainer, "Incorrect password");
                        break;
                    case HTTPStatusCodes.NOT_FOUND:
                        snackbar = Utils.createWhiteSnackBar(SignUpActivity.this, mContainer, "We are sorry, but we did not found you");
                        break;
                }
                if (snackbar != null) {
                    snackbar.getView().setBackgroundColor(Color.WHITE);
                    snackbar.show();
                }
            }
        });

        mFragmentManager.beginTransaction().add(R.id.fragmentContainer, SignUpNameFragment.newInstance()).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMConstants.ACTION_SIGNUP));
        mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMConstants.ACTION_ERROR));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onSaveName(String name) {
        mName = name;
        mFragmentManager.beginTransaction().add(R.id.fragmentContainer, SignUpEmailFragment.newInstance()).addToBackStack(null).commit();
    }

    @Override
    public void onSaveEmail(String email) {
        mProgressDialog.show();
        mEmail = email;
        checkEmail(mEmail);
    }

    @Override
    public void onPasswordSave(String password) {
        mPassword = password;
        LogInRegistrationIntentService.startActionSignup(this,mName, mEmail, mPassword);
        mProgressDialog = ProgressDialog.show(this, "Signup", "Please wait ...", true);
    }

    /**
     * Sends a request to the server to check if the email already exists.
     * If the server has the email, the user already has an account and we should tell him that
     * Else proceed to the next step
     *
     * @param email the email adress to send to the server
     */
    private void checkEmail(String email) {
        String verifyUserURL = null;
        try {
            verifyUserURL = GCMConstants.SERVER_USER_EXISTS +
                            "?email=" + URLEncoder.encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, verifyUserURL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                mFragmentManager.beginTransaction().add(R.id.fragmentContainer, SignUpPassFragment.newInstance()).addToBackStack(null).commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mEmail = null;
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode) {
                        case HTTPStatusCodes.REQUEST_TIMEOUT:
                            Toast.makeText(SignUpActivity.this, "Timeout", Toast.LENGTH_LONG).show();
                            break;
                        case HTTPStatusCodes.REQUEST_CONFLICT:
                            Toast.makeText(SignUpActivity.this, "You already have an account", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                    Log.e("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode);
                }
                if (error instanceof TimeoutError) {
                    Log.e("Volley", "TimeoutError");
                } else if (error instanceof NoConnectionError) {
                    Log.e("Volley", "NoConnectionError");
                } else if (error instanceof AuthFailureError) {
                    Log.e("Volley", "AuthFailureError");
                } else if (error instanceof ServerError) {
                    Log.e("Volley", "ServerError");
                } else if (error instanceof NetworkError) {
                    Log.e("Volley", "NetworkError");
                } else if (error instanceof ParseError) {
                    Log.e("Volley", "ParseError");
                }
            }
        });

        mRequestQueue.add(jsonRequest);
    }


}

