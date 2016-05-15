package com.marked.pixsee.shop.detail;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.marked.pixsee.R;

/**
 * Created by Tudor on 2016-05-11.
 */
class ItemDetailVH extends RecyclerView.ViewHolder {
	private ImageView imageView;
	private int color = Color.parseColor("#64ffffff");

	public ItemDetailVH(View itemView) {
		super(itemView);
		imageView = (ImageView) itemView.findViewById(R.id.item_detail);
	}

	public void bind(ItemDetail itemDetail) {
		imageView.setImageDrawable(itemDetail.getIcon());
		imageView.setBackgroundTintList(ColorStateList.valueOf(color));
	}
}
