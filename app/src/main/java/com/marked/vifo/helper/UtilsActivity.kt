package com.marked.vifo.helper;

import android.content.Context

/**
 * Created by Tudor Pop on 23-Jan-16.
 */
fun Context.Toast(message: CharSequence, duration: Int = android.widget.Toast.LENGTH_SHORT) {
	android.widget.Toast.makeText(this, message, duration).show()
}
