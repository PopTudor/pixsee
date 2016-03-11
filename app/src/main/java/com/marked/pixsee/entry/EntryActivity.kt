package com.marked.pixsee.entry

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.marked.pixsee.friends.FriendsActivity
import com.marked.pixsee.utility.GCMConstants


class EntryActivity : AppCompatActivity() {
	private var mUserRegistered: Boolean = false
	override
	fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mUserRegistered = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, false)
	}

	override
	fun onResume() {
		super.onResume()
		val intent: Intent
		if (mUserRegistered) {
			intent = Intent(this, FriendsActivity::class.java)
		} else {
			intent = Intent(this, AuthActivity::class.java)
		}
		startActivity(intent)
		finish()
	}

}
