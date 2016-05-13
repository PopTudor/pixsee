package com.marked.pixsee.utility;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Tudor Pop on 07-Dec-15.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
	private int space;

	public SpacesItemDecoration(int space) {
		this.space = space;
	}
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		outRect.left = space;
		outRect.right = space;
		outRect.bottom = space;

		// Add top margin only for the first item to avoid double space between items
		if (parent.getChildLayoutPosition(view) == 0)
			outRect.top = space;
	}
}