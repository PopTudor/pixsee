package com.marked.vifo.ui.adapter.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.marked.dotview.BaseDot
import com.marked.dotview.DotView
import com.marked.vifo.R

/**
 * Created by Tudor Pop on 02-Jan-16.
 * This makes the animation dots happens
 */
public class TypingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
	private val dot = itemView.findViewById(R.id.dot) as DotView

	fun start() {
		dot.mBaseDot?.setAnimationStatus(BaseDot.AnimStatus.START)
    }

	fun stop() {
		dot.mBaseDot?.setAnimationStatus(BaseDot.AnimStatus.CANCEL)
	}
}
