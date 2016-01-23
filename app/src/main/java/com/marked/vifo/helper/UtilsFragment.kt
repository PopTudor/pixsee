package com.marked.vifo.helper

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * Created by Tudor Pop on 23-Jan-16.
 */
fun FragmentManager.add(containerId: Int, fragment: Fragment) {
	this.beginTransaction().add(containerId, fragment).commit()
}

fun FragmentManager.addToBackStack(containerId: Int, fragment: Fragment) {
	this.beginTransaction().add(containerId, fragment).addToBackStack(null).commit()
}