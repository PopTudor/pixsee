package com.marked.pixsee.helper

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * Created by Tudor Pop on 23-Jan-16.
 */
fun FragmentManager.add(containerId: Int, fragment: Fragment) {
	this.beginTransaction().add(containerId, fragment).commit()
}

fun FragmentManager.add(containerId: Int, fragment: Fragment, tag: String) {
	this.beginTransaction().add(containerId, fragment, tag).commit()
}

fun FragmentManager.addToBackStack(containerId: Int, fragment: Fragment) {
	this.beginTransaction().add(containerId, fragment).addToBackStack(null).commit()
}