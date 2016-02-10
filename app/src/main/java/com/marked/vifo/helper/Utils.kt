package com.marked.vifo.helper

import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.provider.Settings
import android.support.v7.app.AlertDialog
import org.json.JSONObject

/**
 * Created by Tudor Pop on 15-Nov-15.
 */
object Utils {
	fun isOnline(context: Context): Boolean {
		val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val netInfo = cm.activeNetworkInfo
		return netInfo != null && netInfo.isConnectedOrConnecting
	}

	fun showNoConnectionDialog(ctx1: Context) {
		val ctx = ctx1
		val builder = AlertDialog.Builder(ctx)
		builder.setCancelable(true)
		builder.setMessage("You need to activate cellular or wifi network in order to login.")
		builder.setTitle("No internet !")
		builder.setPositiveButton("Wifi") { dialog, which -> ctx.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
		builder.setNegativeButton("Cellular") { dialog, which ->
			val intent = Intent()
			intent.component = ComponentName("com.android.settings", "com.android.settings.Settings\$DataUsageSummaryActivity")
			ctx.startActivity(intent)
		}

		builder.setNeutralButton("Cancel", DialogInterface.OnClickListener { dialog, which -> return@OnClickListener })

		builder.setOnCancelListener(DialogInterface.OnCancelListener { return@OnCancelListener })

		builder.show()
	}


	fun getNumberDigits(inString: String): Int {
		if (inString.isNullOrBlank()) return 0
		return inString.filter { it.isDigit() }.length
	}
}

