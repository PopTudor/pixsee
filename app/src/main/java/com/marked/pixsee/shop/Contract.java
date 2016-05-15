package com.marked.pixsee.shop;

import android.support.annotation.NonNull;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.shop.list.Category;

import java.util.List;

/**
 * Created by Tudor on 2016-05-11.
 */
public interface Contract {
	interface View extends BaseView<Presenter> {

		void setLoadingIndicator(boolean active);

		void showTasks(List<Category> tasks);

		void showAddTask();

		void showCategoryDetailsUi(Category taskId);

		void showCategoryMarkedComplete();

		void showCategoryMarkedActive();

		void showCompletedCategoryCleared();

		void showLoadingTasksError();

		void showNoTasks();

		void showActiveFilterLabel();

		void showCompletedFilterLabel();

		void showAllFilterLabel();

		void showNoActiveTasks();

		void showNoCompletedTasks();

		void showSuccessfullySavedMessage();

		boolean isActive();

		void showFilteringPopUpMenu();
	}
	interface Presenter extends BasePresenter{
		void result(int requestCode, int resultCode);

		void loadTasks(boolean forceUpdate);

		void addNewTask();

		void openTaskDetails(@NonNull Category requestedTask);

		void completeTask(@NonNull Category completedTask);

		void activateTask(@NonNull Category activeTask);

		void clearCompletedTasks();
	}

}
