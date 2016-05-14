package com.marked.pixsee.shop.di;

import com.marked.pixsee.shop.Contract;
import com.marked.pixsee.shop.ShopPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 2016-05-11.
 */
@Module
public class ShopModule {
	private Contract.View view;
	public ShopModule(Contract.View view) {
		this.view = view;
	}

	@Provides
	public ShopPresenter providesShopPresenter(){
		return new ShopPresenter(view);
	}
}
