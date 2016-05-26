package com.marked.pixsee.main.di;

import com.marked.pixsee.main.MainContract;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-27.
 */
public class MainPresenter implements MainContract.Presenter {
	private WeakReference<MainContract.View> mWeakView;

	public MainPresenter(MainContract.View view) {
		this.mWeakView = new WeakReference<>(view);
		this.mWeakView.get().setPresenter(this);
	}

	@Override
	public void start() {

	}
}
