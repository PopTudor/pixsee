package com.marked.pixsee.shop.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marked.pixsee.R;
import com.marked.pixsee.commons.SpaceItemDecorator;
import com.marked.pixsee.shop.Contract;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.ContextCompat.getDrawable;


/**
 * Created by Tudor on 2016-05-11.
 */
public class ShopListFragment extends Fragment implements Contract.View {

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
	private CategoryListener callback;

	public static ShopListFragment newInstance() {
		return new ShopListFragment();
	}

	public ShopListFragment() {
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			callback = (CategoryListener) context;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
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
		recyclerView.addItemDecoration(new SpaceItemDecorator(getActivity(), R.dimen.item_spacing), 0);
		recyclerView.setAdapter(mCategoryAdapter);
		return root;
	}

	@Override
	public void onStart() {
		super.onStart();
		Category category2 = new Category(getDrawable(getActivity(), R.drawable.ic_emoji_category),
				                                 getDrawable(getActivity(), R.drawable.emoji_category_background),
				                                 "Emojis", "2 Emojis",
				                                 "Feel in a particular way ? Release your emotions with these expressive filters.", 2);
		Category category = new Category(getDrawable(getActivity(), R.drawable.ic_internet),
				                                getDrawable(getActivity(), R.drawable.login_gradient),
				                                "Internet", "1 Item",
				                                "Here you find popular memes on the internet.", 1);
		Category category3 = new Category(null, null, "Gestures", "Coming soon", "Coming soon");
		Category category4 = new Category(null, null, "Box emotions", "Coming soon", "Coming soon");
		Category category5 = new Category(null, null, "Animals", "Coming soon", "Coming soon");
		Category category6 = new Category(null, null, "Plants", "Coming soon", "Coming soon");
		Category category7 = new Category(null, null, "Aliens", "Coming soon", "Coming soon");
		Category category8 = new Category(null, null, "Social", "Coming soon", "Coming soon");
		Category category9 = new Category(null, null, "Spooky", "Coming soon", "Coming soon");
		Category category10 = new Category(null, null, "Anonym", "Coming soon", "Coming soon");
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
	public void showCategoryDetailsUi(Category category) {
		callback.onCategoryClicked(category);
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
