package com.marked.pixsee.store.detail;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.marked.pixsee.R;

public class StoreDetail extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_detail);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(Color.parseColor("#212121"));
		}
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
	}
}
