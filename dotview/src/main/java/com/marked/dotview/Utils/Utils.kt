package com.marked.dotview.Utils

import android.graphics.Paint

/**
 * Created by Tudor Pop on 24-Jan-16.
 */
fun Int.toPaintStyle(): Paint.Style {
	when (this) {
		0 -> {
			return Paint.Style.FILL
		}
		1 -> {
			return Paint.Style.STROKE
		}
		else -> {
			return Paint.Style.FILL_AND_STROKE
		}
	}
}