package com.marked.pixsee.signup;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;

/**
 * Created by Tudor on 13-Jun-16.
 */
public interface SignUpContract {
	interface Presenter extends BasePresenter, SignUpNameFragment.SignUpNameFragmentInteraction,SignUpEmailFragment
			.SignUpEmailFragmentInteraction,SignUpPassFragment.SignUpPassFragmentInteraction{

		void checkEmail(String email);
	}

	interface View extends BaseView<Presenter> {

		void hideDialog();

		void showToast(String s);

		void showSignupStepPassword();

		void showSignupStepName();

		void showSignupStepEmail(String name);

		void showDialog(String title,String message);

		void signupComplete(String name, String email, String username, String password);

		void showMainScreen();
	}
}

