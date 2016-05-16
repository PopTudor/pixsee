package com.marked.pixsee.shop.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marked.pixsee.R;

import java.util.ArrayList;
import java.util.List;

public class StoreDetail extends Fragment {
	public static final String CATEGORY_TAG = "CATEGORY_TAG";
	public static final String CATEGORY_DESCRIPTION = "CATEGORY_DESCRIPTION";
	public static final String CATEGORY_NUM_OF_ITEMS = "CATEGORY_NUM_ITEMS";
	public static final String CATEGORY_TITLE = "CATEGORY_TITLE";
	public static final String CATEGORY_ID = "CATEGORY_ID";

	private ItemDetailAdapter itemDetailAdapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_shop_detail, container, false);
		((TextView) rootView.findViewById(R.id.descriptionTextview)).setText(getArguments().getString(CATEGORY_DESCRIPTION));
		String numOfItems = String.valueOf(getArguments().getInt(CATEGORY_NUM_OF_ITEMS, -1));
		String title = getArguments().getString(CATEGORY_TITLE);


		List<ItemDetail> itemDetails = new ArrayList<>();
		if (title.equalsIgnoreCase("internet")) {
			itemDetails.add(new ItemDetail(ContextCompat.getDrawable(getActivity(), android.R.mipmap.sym_def_app_icon)));
		} else {
			itemDetails.add(new ItemDetail(ContextCompat.getDrawable(getActivity(), R.drawable.ic_mlg)));
			itemDetails.add(new ItemDetail(ContextCompat.getDrawable(getActivity(), R.drawable.ic_hearts)));
		}
		itemDetails.add(new ItemDetail(ContextCompat.getDrawable(getActivity(), R.drawable.ic_lock_24dp)));
		itemDetailAdapter = new ItemDetailAdapter(itemDetails);
		// Set up the toolbar.
		try {
			((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_24dp);
			((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
			if (Integer.parseInt(numOfItems) != -1) {
				((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(String.valueOf(numOfItems));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.itemDetailRecyclerview);
		recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
		recyclerView.setAdapter(itemDetailAdapter);
		return rootView;
	}

	public static StoreDetail newInstance(String id, String description, String title, int numOfItems) {
		Bundle bundle = new Bundle();
		bundle.putString(CATEGORY_ID, id);
		bundle.putString(CATEGORY_DESCRIPTION, description);
		bundle.putString(CATEGORY_TITLE, title);
		bundle.putInt(CATEGORY_NUM_OF_ITEMS, numOfItems);
		StoreDetail storeDetail = new StoreDetail();
		storeDetail.setArguments(bundle);
		return storeDetail;
	}

	public StoreDetail() {
	}
}
