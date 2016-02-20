package com.marked.vifo.helper

import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.provider.Settings
import android.support.v7.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.marked.vifo.model.contact.Contact
import com.marked.vifo.model.database.database
import org.jetbrains.anko.async
import org.jetbrains.anko.db.transaction

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
/**
 * Save the specified JsonArray to the specified table string
 * It has to be a Contact array for now
 * @param table In which table to save the array
 * @param jsonArray the array to save
 * */
fun Context.saveToTable(table: String, jsonArray: JsonArray) {
    async() {
        database.use {
            transaction {
                val gson = Gson()
                jsonArray.forEach {
                    insertWithOnConflict(table, null,
                            gson.fromJson(it, Contact::class.java).toContentValues(),
                            SQLiteDatabase.CONFLICT_IGNORE)
                }
            }
        }
    }
}