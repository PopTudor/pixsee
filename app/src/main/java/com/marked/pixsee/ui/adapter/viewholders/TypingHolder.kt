package com.marked.pixsee.ui.adapter.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.message_typing.view.*

/**
 * Created by Tudor Pop on 02-Jan-16.
 * This makes the animation dots happens
 */
class TypingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
	fun start() = itemView.dot.showNow()
	fun stop() = itemView.dot.hideNow()
}
