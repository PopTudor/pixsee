package com.marked.pixsee.signup;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;

/**
 * Created by Tudor on 13-Jun-16.
 */
public interface SignupContract {
	interface Presenter extends BasePresenter{

		void checkEmail(String email);
	}

	interface View extends BaseView<Presenter> {

		void hideDialog();

		void showToast(String s);

		void showSignUpPasswordStep();

	}
}

