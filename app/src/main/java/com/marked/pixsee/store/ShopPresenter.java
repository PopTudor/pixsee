package com.marked.pixsee.store;

import android.support.annotation.NonNull;

import com.marked.pixsee.store.data.Category;


/**
 * Created by Tudor on 2016-05-10.
 */
public class ShopPresenter implements Contract.Presenter {
	private Contract.View view;

	public ShopPresenter(Contract.View view) {
		this.view = view;

		this.view.setPresenter(this);
	}

	@Override
	public void start() {

	}

	@Override
	public void result(int requestCode, int resultCode) {

	}

	@Override
	public void loadTasks(boolean forceUpdate) {

	}

	@Override
	public void addNewTask() {

	}

	@Override
	public void openTaskDetails(@NonNull Category requestedTask) {

	}

	@Override
	public void completeTask(@NonNull Category completedTask) {

	}

	@Override
	public void activateTask(@NonNull Category activeTask) {

	}

	@Override
	public void clearCompletedTasks() {

	}
}
