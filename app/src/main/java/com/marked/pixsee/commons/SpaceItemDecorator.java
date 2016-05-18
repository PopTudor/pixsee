package com.marked.pixsee.commons;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Tudor on 2016-05-15.
 */
public class SpaceItemDecorator extends RecyclerView.ItemDecoration {

	private int mItemOffset;

	public SpaceItemDecorator(int itemOffset) {
		mItemOffset = itemOffset;
	}

	public SpaceItemDecorator(@NonNull Context context, @DimenRes int itemOffsetId) {
		this(context.getResources().getDimensionPixelSize(itemOffsetId));
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
	                           RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
	}
}
