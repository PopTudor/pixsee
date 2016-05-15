package com.marked.pixsee.shop.detail;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.marked.pixsee.R;
import com.marked.pixsee.shop.list.ShopListFragment;

import java.util.ArrayList;
import java.util.List;

public class StoreDetail extends AppCompatActivity {
	private ItemDetailAdapter itemDetailAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_detail);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(Color.parseColor("#212121"));
		}
		((TextView)findViewById(R.id.descriptionTextview)).setText(getIntent().getStringExtra(ShopListFragment.CATEGORY_DESCRIPTION));
		List<ItemDetail> itemDetails = new ArrayList<>();
		itemDetails.add(new ItemDetail(ContextCompat.getDrawable(this, R.drawable.ic_mlg)));
		itemDetails.add(new ItemDetail(ContextCompat.getDrawable(this, R.drawable.ic_hearts)));
		itemDetails.add(new ItemDetail(ContextCompat.getDrawable(this, android.R.mipmap.sym_def_app_icon)));
		itemDetails.add(new ItemDetail(ContextCompat.getDrawable(this, R.drawable.ic_lock_24dp)));
		itemDetailAdapter = new ItemDetailAdapter(itemDetails);
		// Set up the toolbar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		try {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_24dp);
			getSupportActionBar().setTitle("Emotions Market");
			toolbar.setTitle("Internet");
			toolbar.setSubtitle("3 items");
			toolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.white));
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.itemDetailRecyclerview);
		recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//		recyclerView.addItemDecoration(new SpaceItemDecorator(this,R.dimen.item_spacing));
		recyclerView.setAdapter(itemDetailAdapter);
	}
}
