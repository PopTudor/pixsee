package com.marked.pixsee.store.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.repacked.antlr.v4.runtime.misc.NotNull;
import com.marked.pixsee.R;
import com.marked.pixsee.store.Contract;
import com.marked.pixsee.store.data.Category;

import java.util.List;


/**
 * Created by Tudor on 2016-05-11.
 */
public class ShopListFragment extends Fragment implements Contract.View {
	private Contract.Presenter mPresenter;
	private CategoryAdapter mCategoryAdapter;

	public static ShopListFragment newInstance(){
		return new ShopListFragment();
	}

	public ShopListFragment() {
	}
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCategoryAdapter = new CategoryAdapter();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.shop_list, container, false);

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		mPresenter.start();
	}

	@Override
	public void setLoadingIndicator(boolean active) {

	}

	@Override
	public void showTasks(List<Category> tasks) {

	}

	@Override
	public void showAddTask() {

	}

	@Override
	public void showTaskDetailsUi(String taskId) {

	}

	@Override
	public void showTaskMarkedComplete() {

	}

	@Override
	public void showTaskMarkedActive() {

	}

	@Override
	public void showCompletedTasksCleared() {

	}

	@Override
	public void showLoadingTasksError() {

	}

	@Override
	public void showNoTasks() {

	}

	@Override
	public void showActiveFilterLabel() {

	}

	@Override
	public void showCompletedFilterLabel() {

	}

	@Override
	public void showAllFilterLabel() {

	}

	@Override
	public void showNoActiveTasks() {

	}

	@Override
	public void showNoCompletedTasks() {

	}

	@Override
	public void showSuccessfullySavedMessage() {

	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public void showFilteringPopUpMenu() {

	}

	@Override
	public void setPresenter(@NotNull Contract.Presenter presenter) {
		mPresenter =presenter;
	}
}
