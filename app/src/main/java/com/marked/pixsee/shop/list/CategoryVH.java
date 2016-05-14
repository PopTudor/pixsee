package com.marked.pixsee.shop.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marked.pixsee.R;
import com.marked.pixsee.shop.data.Category;

/**
 * Created by Tudor on 2016-05-11.
 */
public class CategoryVH extends RecyclerView.ViewHolder {
	private LinearLayout container;
	private ImageView simpleDraweeView;
	private TextView title;
	private TextView subtitle;

	public CategoryVH(View itemView) {
		super(itemView);
		container = (LinearLayout) itemView.findViewById(R.id.container);
		simpleDraweeView = (ImageView) itemView.findViewById(R.id.categoryIcon);
		title = (TextView) itemView.findViewById(R.id.categoryTitleTextView);
		subtitle = (TextView) itemView.findViewById(R.id.categorySubtitleTextview);
	}

	public void bind(Category category) {
		title.setText(category.getTitle());
		subtitle.setText(category.getSubtitle());
		if (category.getBackground() == null)
			return;
		title.setAlpha(1.0f);
		subtitle.setAlpha(1.0f);
		container.setBackground(category.getBackground());
		simpleDraweeView.setImageDrawable(category.getIcon());
		simpleDraweeView.setAlpha(1.f);
	}
}
