package com.marked.vifo.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.marked.vifo.R
import kotlinx.android.synthetic.main.activity_entry_auth.*

class EntryAuthActivity : AppCompatActivity(), View.OnClickListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_entry_auth)

		entryLogInButton.setOnClickListener(this)
		entrySignUpButton.setOnClickListener(this)

		checkPlayServices()// check if the user has google play services, else finish
	}


	override fun onClick(v: View) {
		when (v.id) {
			R.id.entryLogInButton -> startActivity(Intent(this, LogInActivity::class.java))
			R.id.entrySignUpButton -> startActivity(Intent(this, SignUpActivity::class.java))
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private fun checkPlayServices(): Boolean {
		val apiAvailability = GoogleApiAvailability.getInstance()
		val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show()
			} else {
				Log.d("***", "This device is not supported.")
				finish()
			}
			return false
		}
		return true
	}

	companion object {
		private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
	}

}
