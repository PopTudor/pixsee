package com.marked.vifo.helper

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Tudor Pop on 07-Dec-15.
 */
class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
		outRect.left = space
		outRect.right = space
		outRect.bottom = space

		// Add top margin only for the first item to avoid double space between items
		if (parent.getChildLayoutPosition(view) == 0)
			outRect.top = space
	}
}