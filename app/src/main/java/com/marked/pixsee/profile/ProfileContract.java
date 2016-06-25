package com.marked.pixsee.profile;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.data.repository.user.User;

import java.io.File;
import java.util.List;

/**
 * Created by Tudor on 06-Jun-16.
 */
interface ProfileContract {
	interface Presenter extends BasePresenter{
		void saveAppUser(User user);

		void logOut();

		void picturesData(File[] list);
	}

	interface View extends BaseView<Presenter> {

		void setData(List<String> list);

	}
}
