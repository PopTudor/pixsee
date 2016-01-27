package com.marked.vifo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity

import com.crashlytics.android.Crashlytics
import com.marked.vifo.extra.GCMConstants

import io.fabric.sdk.android.Fabric


class EntryActivity : AppCompatActivity() {
	private var mUserRegistered: Boolean = false
	override
	fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val fabric = Fabric.Builder(this)
				.kits(Crashlytics())
				.debuggable(true) // TODO: 13-Dec-15 disable this
				.build()
		Fabric.with(fabric)

		mUserRegistered = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, false)
	}

	override
	fun onResume() {
		super.onResume()
		val intent: Intent
		if (mUserRegistered) {
			intent = Intent(this, ContactListActivity::class.java)
		} else {
			intent = Intent(this, EntryAuthActivity::class.java)
		}
		startActivity(intent)
		finish()
	}

}
