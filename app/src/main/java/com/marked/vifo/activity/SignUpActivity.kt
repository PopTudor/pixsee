package com.marked.vifo.activity

import android.app.ProgressDialog
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.marked.vifo.R
import com.marked.vifo.delegate.DialogRegistration
import com.marked.vifo.extra.GCMConstants
import com.marked.vifo.extra.HTTPStatusCodes
import com.marked.vifo.extra.ServerConstants
import com.marked.vifo.fragment.signup.SignUpEmailFragment
import com.marked.vifo.fragment.signup.SignUpNameFragment
import com.marked.vifo.fragment.signup.SignUpPassFragment
import com.marked.vifo.gcm.RegistrationBroadcastReceiver
import com.marked.vifo.gcm.service.LogInRegistrationIntentService
import com.marked.vifo.helper.Toast
import com.marked.vifo.helper.add
import com.marked.vifo.helper.addToBackStack
import com.marked.vifo.model.RequestQueue
import java.net.URLEncoder

class SignUpActivity : AppCompatActivity(), SignUpNameFragment.SignUpNameFragmentInteraction, SignUpEmailFragment.SignUpEmailFragmentInteraction, SignUpPassFragment.SignUpPassFragmentInteraction {
	private val mFragmentManager by lazy { supportFragmentManager }
	private val mProgressDialog by lazy { ProgressDialog(this) }
	private val mRegistrationBroadcastReceiver by lazy { RegistrationBroadcastReceiver(DialogRegistration(this, mProgressDialog)) }
	private val mBroadcastManagerastManager by lazy { LocalBroadcastManager.getInstance(this) }
	private val mRequestQueue by lazy { RequestQueue.getInstance(this) }

	private var mName: String? = null
	private var mEmail: String? = null
	private var mPassword: String? = null

	override
	fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_sign_up)

		mFragmentManager.add(R.id.fragmentContainer, SignUpNameFragment.newInstance())
	}

	override fun onResume() {
		super.onResume()
		mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, IntentFilter(GCMConstants.ACTION_SIGNUP))
		mBroadcastManagerastManager.registerReceiver(mRegistrationBroadcastReceiver, IntentFilter(GCMConstants.ACTION_ERROR))
	}

	override
	fun onPause() {
		mBroadcastManagerastManager.unregisterReceiver(mRegistrationBroadcastReceiver)
		super.onPause()
	}

	override
	fun onSaveName(name: String) {
		mName = name
		mFragmentManager.addToBackStack(R.id.fragmentContainer, SignUpEmailFragment.newInstance(mName))
	}

	override fun onSaveEmail(email: String) {
		mProgressDialog.show()
		mEmail = email
		checkEmail(mEmail)
	}

	override fun onPasswordSave(password: String) {
		mPassword = password
		LogInRegistrationIntentService.startActionSignup(this, mName, mEmail, mPassword)
		mProgressDialog.setTitle("Signup")
		mProgressDialog.setMessage("Please wait...")
		mProgressDialog.isIndeterminate = true
		mProgressDialog.show()
	}

	/**
	 * Sends a request to the server to check if the email already exists.
	 * If the server has the email, the user already has an account and we should tell him that
	 * Else proceed to the next step

	 * @param email the email adress to send to the server
	 */
	private fun checkEmail(email: String?) {
		var verifyUserURL = "${ServerConstants.SERVER_USER_EXISTS}?email=${URLEncoder.encode(email, "UTF-8")}"

		val jsonRequest = JsonObjectRequest(Request.Method.GET, verifyUserURL, Response.Listener<org.json.JSONObject> {
			mProgressDialog.dismiss()
			mFragmentManager.addToBackStack(R.id.fragmentContainer, SignUpPassFragment.newInstance())
		}, Response.ErrorListener { error ->
			mProgressDialog.dismiss()
			mEmail = null
			val networkResponse = error.networkResponse
			when (networkResponse?.statusCode) {
				HTTPStatusCodes.REQUEST_TIMEOUT -> Toast("Timeout")
				HTTPStatusCodes.REQUEST_CONFLICT -> Toast("You already have an account")
			}
			Log.e("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode)
			when (error) {
				is TimeoutError ->
					Log.e("Volley", "TimeoutError")
				is NoConnectionError ->
					Log.e("Volley", "NoConnectionError")
				is AuthFailureError ->
					Log.e("Volley", "AuthFailureError")
				is ServerError ->
					Log.e("Volley", "ServerError")
				is NetworkError ->
					Log.e("Volley", "NetworkError")
				is ParseError -> Log.e("Volley", "ParseError")
			}
		})

		mRequestQueue.add(jsonRequest)
	}


}

