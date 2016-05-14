package com.marked.pixsee.shop.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;
import com.marked.pixsee.shop.data.Category;

import java.util.List;

/**
 * Created by Tudor on 2016-05-11.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryVH> {
	private List<Category> mDataSet;
	private ShopListFragment.CategoryListener categoryListener;

	public CategoryAdapter(List<Category> mDataSet, ShopListFragment.CategoryListener listener) {
		this.mDataSet = mDataSet;
		this.categoryListener = listener;
	}


	@Override
	public CategoryVH onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
		return new CategoryVH(view);
	}

	@Override
	public void onBindViewHolder(final CategoryVH holder, int position) {
		holder.bind(mDataSet.get(position));
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				categoryListener.onCategoryClicked(mDataSet.get(holder.getAdapterPosition()));
			}
		});
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return mDataSet.size();
	}

	public void replaceData(List<Category> categories) {
		setList(categories);
		notifyDataSetChanged();
	}

	public void setList(List<Category> list) {
		this.mDataSet = list;
	}
}
