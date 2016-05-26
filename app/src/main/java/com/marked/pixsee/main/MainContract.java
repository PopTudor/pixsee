package com.marked.pixsee.main;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.commands.Command;

/**
 * Created by Tudor on 2016-05-27.
 */
public interface MainContract {
	interface Presenter extends BasePresenter {
		void execute(Command command);

		void chatClicked();
	}

	interface View extends BaseView<Presenter> {
		void displayChat(boolean show);
	}
}
