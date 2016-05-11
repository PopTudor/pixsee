package com.marked.pixsee.store.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.marked.pixsee.R;
import com.marked.pixsee.store.data.Category;

/**
 * Created by Tudor on 2016-05-11.
 */
public class CategoryVH extends RecyclerView.ViewHolder {
	private LinearLayout container;
	private SimpleDraweeView simpleDraweeView;
	private TextView title;
	private TextView subtitle;

	public CategoryVH(View itemView) {
		super(itemView);
		container = (LinearLayout) itemView.findViewById(R.id.container);
		simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.categoryIcon);
		title = (TextView) itemView.findViewById(R.id.categoryTitleTextView);
		subtitle = (TextView) itemView.findViewById(R.id.categorySubtitleTextview);
	}
	public void bind(Category category) {
		container.setBackground(category.getBackground());
		simpleDraweeView.setImageDrawable(category.getIcon());
		title.setText(category.getTitle());
		subtitle.setText(category.getSubtitle());
	}
}
