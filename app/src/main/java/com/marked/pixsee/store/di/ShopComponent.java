package com.marked.pixsee.store.di;

import com.marked.pixsee.injection.scopes.PerActivity;
import com.marked.pixsee.store.ShopActivity;

import dagger.Component;

/**
 * Created by Tudor on 2016-05-11.
 */
@Component(modules = {ShopModule.class})
@PerActivity
public interface ShopComponent {
	void inject(ShopActivity shopListActivity);
}
