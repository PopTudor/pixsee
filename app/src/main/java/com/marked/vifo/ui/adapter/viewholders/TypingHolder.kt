package com.marked.vifo.ui.adapter.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_typing_message.view.*

/**
 * Created by Tudor Pop on 02-Jan-16.
 * This makes the animation dots happens
 */
class TypingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//	private var dot = itemView.findViewById(R.id.dot) as DilatingDotsProgressBar

	fun start() {
		itemView.dot.showNow()
//		dot.mBaseDot.setAnimationStatus(BaseDot.AnimStatus.START)
    }

	fun stop() {
		itemView.dot.hideNow()
//		dot.mBaseDot.setAnimationStatus(BaseDot.AnimStatus.CANCEL)
	}
}
