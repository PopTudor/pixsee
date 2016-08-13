package com.marked.pixsee.selfie;

import android.graphics.SurfaceTexture;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.commands.Command;

/**
 * Created by Tudor on 2016-05-22.
 */
class SelfieContract {
	interface Presenter extends BasePresenter {
		void execute(Command command);

		void takePicture();

		void resumeSelfie();

		void onAvailableCameraSurfaceTexture(SurfaceTexture cameraSurfaceTexture);
	}

	interface View extends BaseView<Presenter> {
		void showTakenPictureActions();

		void displayEmojiActions(boolean showSelfieActions);
	}
}
