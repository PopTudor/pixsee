package com.marked.vifo.helper

import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
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
			intent.setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$DataUsageSummaryActivity"))
			ctx.startActivity(intent)
		}

		builder.setNeutralButton("Cancel", DialogInterface.OnClickListener { dialog, which -> return@OnClickListener })

		builder.setOnCancelListener(DialogInterface.OnCancelListener { return@OnCancelListener })

		builder.show()
	}

	fun isEmpty(string: String?): Boolean {
		return string.isNullOrBlank()
	}

	fun getNumberDigits(inString: String): Int {
		if (isEmpty(inString)) {
			return 0
		}
		var numDigits = 0
		val length = inString.length
		for (i in 0..length - 1)
			if (Character.isDigit(inString[i]))
				numDigits++
		return numDigits
	}

	fun JSONObject.toJSON(string: String?) {
		val tmp = JSONObject(string)
	}
}

fun Handler.uiThread(body: () -> Unit) {
	Handler(Looper.getMainLooper()).post(Runnable(body))
}
