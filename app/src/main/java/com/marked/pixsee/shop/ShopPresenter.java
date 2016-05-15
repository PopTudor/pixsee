package com.marked.pixsee.shop;

import android.support.annotation.NonNull;

import com.marked.pixsee.shop.list.Category;


/**
 * Created by Tudor on 2016-05-10.
 */
public class ShopPresenter implements Contract.Presenter {
	private Contract.View mView;

	public ShopPresenter(Contract.View view) {
		this.mView = view;

		this.mView.setPresenter(this);
	}

	@Override
	public void start() {
		loadTasks(false);
	}

	@Override
	public void result(int requestCode, int resultCode) {

	}

	@Override
	public void loadTasks(boolean forceUpdate) {
		if (forceUpdate){

		}else {
//			final List<Category> categories = new ArrayList<>(3);
//			Observable.from(new ArrayList<Category>(new Category()){})
//					.subscribe(new Subscriber<Category>() {
//						@Override
//						public void onCompleted() {
//							mView.showTasks(categories);
//						}
//
//						@Override
//						public void onError(Throwable e) {
//							mView.showLoadingTasksError();
//						}
//
//						@Override
//						public void onNext(Category category) {
//							categories.add(category);
//						}
//					});
		}

	}

	@Override
	public void addNewTask() {

	}

	@Override
	public void openTaskDetails(@NonNull Category requestedTask) {
		mView.showCategoryDetailsUi(requestedTask);
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
