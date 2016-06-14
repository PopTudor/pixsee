package com.marked.pixsee.login;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;

/**
 * Created by Tudor on 14-Jun-16.
 */
public interface LoginContract {
	interface Presenter extends BasePresenter{
		public void handleLogin(String email, String password);
	}

	interface View extends BaseView<Presenter> {

	}
}
