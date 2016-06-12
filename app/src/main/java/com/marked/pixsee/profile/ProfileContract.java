package com.marked.pixsee.profile;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.data.repository.user.User;

/**
 * Created by Tudor on 06-Jun-16.
 */
public interface ProfileContract {
	interface Presenter extends BasePresenter{
		void saveAppUser(User user);

		void logOut();
	}

	interface View extends BaseView<Presenter> {

	}
}
