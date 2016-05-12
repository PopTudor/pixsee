package com.marked.pixsee.store.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.repacked.antlr.v4.runtime.misc.NotNull;
import com.marked.pixsee.R;
import com.marked.pixsee.store.Contract;
import com.marked.pixsee.store.data.Category;
import com.marked.pixsee.store.detail.StoreDetail;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.ContextCompat.getDrawable;


/**
 * Created by Tudor on 2016-05-11.
 */
public class ShopListFragment extends Fragment implements Contract.View {
	public static final String CATEGORY_TAG = "CATEGORY_TAG";
	private Contract.Presenter mPresenter;
	private CategoryAdapter mCategoryAdapter;
	private CategoryListener mCategoryListener = new CategoryListener() {
		@Override
		public void onCategoryClicked(Category category) {
			mPresenter.openTaskDetails(category);
		}

		@Override
		public void onCompleteCategoryClick(Category completedCategory) {
			mPresenter.completeTask(completedCategory);
		}

		@Override
		public void onActivateCategoryClick(Category activatedCategory) {
			mPresenter.activateTask(activatedCategory);
		}
	};

	public static ShopListFragment newInstance() {
		return new ShopListFragment();
	}

	public ShopListFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCategoryAdapter = new CategoryAdapter(new ArrayList<Category>(0), mCategoryListener);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_shop_list, container, false);
		GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
		RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.categoryRecyclerview);
		recyclerView.setLayoutManager(gridLayoutManager);
		recyclerView.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.item_spacing), 0);
		recyclerView.setAdapter(mCategoryAdapter);
		return root;
	}

	public static class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

		private int mItemOffset;

		public ItemOffsetDecoration(int itemOffset) {
			mItemOffset = itemOffset;
		}

		public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
			this(context.getResources().getDimensionPixelSize(itemOffsetId));
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
		                           RecyclerView.State state) {
			super.getItemOffsets(outRect, view, parent, state);
			outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Category category = new Category(getDrawable(getActivity(), R.drawable.ic_internet),
				                                getDrawable(getActivity(), R.drawable.login_gradient),
				                                "Internet", "3 Items");
		Category category2 = new Category(null, null, "Emojis", "Coming soon");
		Category category3 = new Category(null, null, "Gestures", "Coming soon");
		Category category4 = new Category(null, null, "Box emotions", "Coming soon");
		Category category5 = new Category(null, null, "Animals", "Coming soon");
		Category category6 = new Category(null, null, "Plants", "Coming soon");
		Category category7 = new Category(null, null, "Aliens", "Coming soon");
		Category category8 = new Category(null, null, "Social", "Coming soon");
		Category category9 = new Category(null, null, "Spooky", "Coming soon");
		Category category10 = new Category(null, null, "Anonym", "Coming soon");
		List<Category> categories = new ArrayList<>();
		categories.add(category);
		categories.add(category2);
		categories.add(category3);
		categories.add(category4);
		categories.add(category5);
		categories.add(category6);
		categories.add(category7);
		categories.add(category8);
		categories.add(category9);
		categories.add(category10);
		mCategoryAdapter.replaceData(categories);
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
	public void showCategoryDetailsUi(String taskId) {
		Intent intent = new Intent(getActivity(), StoreDetail.class);
		intent.putExtra(CATEGORY_TAG, taskId);
		startActivity(intent);
	}

	@Override
	public void showCategoryMarkedComplete() {

	}

	@Override
	public void showCategoryMarkedActive() {

	}

	@Override
	public void showCompletedCategoryCleared() {

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
		mPresenter = presenter;
	}

	public interface CategoryListener {
		void onCategoryClicked(Category category);

		void onCompleteCategoryClick(Category completedCategory);

		void onActivateCategoryClick(Category activatedCategory);
	}
}
