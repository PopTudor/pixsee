package com.marked.vifo.ui.activity

import android.app.ProgressDialog
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import com.google.gson.JsonObject
import com.marked.vifo.R
import com.marked.vifo.delegate.DialogRegistration
import com.marked.vifo.extra.GCMConstants
import com.marked.vifo.extra.HTTPStatusCodes.REQUEST_CONFLICT
import com.marked.vifo.extra.HTTPStatusCodes.REQUEST_TIMEOUT
import com.marked.vifo.extra.ServerConstants
import com.marked.vifo.gcm.RegistrationBroadcastReceiver
import com.marked.vifo.gcm.service.LogInRegistrationIntentService
import com.marked.vifo.helper.add
import com.marked.vifo.helper.addToBackStack
import com.marked.vifo.ui.fragment.signup.SignUpEmailFragment
import com.marked.vifo.ui.fragment.signup.SignUpNameFragment
import com.marked.vifo.ui.fragment.signup.SignUpPassFragment
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.net.URLEncoder

class SignUpActivity : AppCompatActivity(), SignUpNameFragment.SignUpNameFragmentInteraction, SignUpEmailFragment.SignUpEmailFragmentInteraction, SignUpPassFragment.SignUpPassFragmentInteraction {
    private val mFragmentManager by lazy { supportFragmentManager }
    private val mProgressDialog by lazy { ProgressDialog(this) }
    private val mRegistrationBroadcastReceiver by lazy { RegistrationBroadcastReceiver(DialogRegistration(this, mProgressDialog)) }
    private val mBroadcastManagerastManager by lazy { LocalBroadcastManager.getInstance(this) }

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
        mName = URLEncoder.encode(name, "UTF-8")
        mFragmentManager.addToBackStack(R.id.fragmentContainer, SignUpEmailFragment.newInstance(name))
    }

    override fun onSaveEmail(email: String) {
        mProgressDialog.show()
        mEmail = URLEncoder.encode(email, "UTF-8")
        checkEmail(mEmail)
    }

    override fun onPasswordSave(password: String) {
        mPassword = URLEncoder.encode(password, "UTF-8")
        LogInRegistrationIntentService.startActionSignup(this, mName, mEmail, mPassword)
        mProgressDialog.setTitle("Signup")
        mProgressDialog.setMessage("Please wait...")
        mProgressDialog.isIndeterminate = true
        mProgressDialog.show()
    }

    /**
     * Sends a request to the server to check if the email already hasAccount.
     * If the server has the email, the user already has an account and we should tell him that
     * Else proceed to the next step
     * @param email the email adress to send to the server
     */
    private fun checkEmail(email: String?) {
        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ServerConstants.SERVER)
                .build()
        val service = retrofit.create(com.marked.vifo.networking.LoginAPI::class.java)
        service.hasAccount(email)
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
                        mProgressDialog.dismiss()
                        if (response?.isSuccess == true) {
                            mFragmentManager.addToBackStack(R.id.fragmentContainer, SignUpPassFragment.newInstance())
                        } else
                            errorStatusCode(response?.code())
                    }

                    override fun onFailure(call: Call<JsonObject>?, error: Throwable?) {
                        mProgressDialog.dismiss()
                        when (error) {
                            is SocketTimeoutException -> toast("Timeout Error")
                        }
                        error?.printStackTrace()
                    }
                })
    }

    fun errorStatusCode(statusCode: Int?): Unit {
        when (statusCode) {
            REQUEST_TIMEOUT -> toast("Timeout")
            REQUEST_CONFLICT -> toast("You already have an account")
        }
    }
}

