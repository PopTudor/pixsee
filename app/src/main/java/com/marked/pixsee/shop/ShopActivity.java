package com.marked.pixsee.shop;

import android.app.SearchManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.marked.pixsee.R;
import com.marked.pixsee.shop.detail.StoreDetail;
import com.marked.pixsee.shop.di.DaggerShopComponent;
import com.marked.pixsee.shop.di.ShopModule;
import com.marked.pixsee.shop.list.Category;
import com.marked.pixsee.shop.list.ShopListFragment;

import javax.inject.Inject;

public class ShopActivity extends AppCompatActivity implements ShopListFragment.CategoryListener {
	@Inject
	ShopPresenter shopPresenter;
	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);
		// Set up the toolbar.
		toolbar = (Toolbar) findViewById(R.id.shopToolbar);
		setSupportActionBar(toolbar);
		setToolbarStyles(toolbar);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		ShopListFragment listFragment = (ShopListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
		if (listFragment == null) {
			listFragment = ShopListFragment.newInstance();
			getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, listFragment).commit();
		}
		DaggerShopComponent.builder()
				.shopModule(new ShopModule(listFragment))
				.build()
				.inject(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_shop, menu);
		// Retrieve the SearchView and plug it into SearchManager
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
		SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		return true;
	}

	private void setToolbarStyles(Toolbar toolbar) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_24dp);
		getSupportActionBar().setTitle("Emotions Market");
		getSupportActionBar().setSubtitle("");
		toolbar.setTitle("Emotions Market");
		toolbar.setSubtitleTextColor(Color.WHITE);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setToolbarStyles(toolbar);
	}

	@Override
	public void onCategoryClicked(Category category) {
		StoreDetail storeDetail = StoreDetail.newInstance(category.getId(), category.getDescription(), category.getTitle(), category.getNumOfItems());
		getSupportFragmentManager().beginTransaction().add(R.id.fullscreenFragmentContainer, storeDetail).addToBackStack(null).commit();
	}

	@Override
	public void onCompleteCategoryClick(Category completedCategory) {

	}

	@Override
	public void onActivateCategoryClick(Category activatedCategory) {

	}
}