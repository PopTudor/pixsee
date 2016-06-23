package com.marked.pixsee.profile;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.marked.pixsee.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 23-Jun-16.
 */
class PictureAdapter extends RecyclerView.Adapter{
	private static List<String> mDataset = new ArrayList<>();

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);
		return new ViewHolder(view,parent.getContext());
	}

	static void setDataset(List<String> mDataset) {
		PictureAdapter.mDataset.clear();
		PictureAdapter.mDataset.addAll(mDataset);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		((ViewHolder) holder).bind(mDataset.get(position));
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	private static class ViewHolder extends RecyclerView.ViewHolder{
		private WeakReference<Context> mContext;
		private SimpleDraweeView mSimpleDraweeView;
		public ViewHolder(View itemView, Context context) {
			super(itemView);
			mContext = new WeakReference<>(context);
			mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.picture);
		}

		void bind(String imagePath) {
			final Uri url = new Uri.Builder()
					.scheme(UriUtil.LOCAL_FILE_SCHEME)
					.path(imagePath)
					.build();
			mSimpleDraweeView.setImageURI(url, mContext);
		}
	}
}
