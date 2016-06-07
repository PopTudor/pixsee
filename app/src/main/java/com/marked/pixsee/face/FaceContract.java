package com.marked.pixsee.face;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.face.custom.CameraSource;

/**
 * Created by Tudor on 2016-05-22.
 */
public interface FaceContract {
	interface Presenter extends BasePresenter, CameraSource.ShutterCallback {
		void execute(Command command);

		void displayActions(boolean b);
	}

	interface View extends BaseView<Presenter> {
		void showTakenPictureActions();

		void displayActions(boolean showSelfieActions);
	}
}
