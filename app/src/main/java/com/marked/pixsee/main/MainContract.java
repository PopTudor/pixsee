package com.marked.pixsee.main;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;

/**
 * Created by Tudor on 2016-05-27.
 */
public interface MainContract {
	interface Presenter extends BasePresenter{

	}

	interface View extends BaseView<Presenter> {

	}
}
