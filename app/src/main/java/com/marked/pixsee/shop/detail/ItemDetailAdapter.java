package com.marked.pixsee.shop.detail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;

import java.util.List;

/**
 * Created by Tudor on 2016-05-11.
 */
class ItemDetailAdapter extends RecyclerView.Adapter<ItemDetailVH> {
	private List<ItemDetail> mDataSet;

	public ItemDetailAdapter(List<ItemDetail> mDataSet) {
		this.mDataSet = mDataSet;
	}


	@Override
	public ItemDetailVH onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
		return new ItemDetailVH(view);
	}

	@Override
	public void onBindViewHolder(final ItemDetailVH holder, int position) {
		holder.bind(mDataSet.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return mDataSet.size();
	}

	public void replaceData(List<ItemDetail> categories) {
		setList(categories);
		notifyDataSetChanged();
	}

	public void setList(List<ItemDetail> list) {
		this.mDataSet = list;
	}
}
