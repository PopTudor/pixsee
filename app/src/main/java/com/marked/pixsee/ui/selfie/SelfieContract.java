package com.marked.pixsee.ui.selfie;

import android.graphics.SurfaceTexture;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.ui.commands.Command;

import org.rajawali3d.renderer.ISurfaceRenderer;

/**
 * Created by Tudor on 2016-05-22.
 */
class SelfieContract {
	interface Presenter extends BasePresenter {
		void execute(Command command);

		void takePicture();

		void resumeSelfie();

		void onAvailableCameraSurfaceTexture(SurfaceTexture cameraSurfaceTexture, int width, int height);

		ISurfaceRenderer getRenderer();

		void release();
	}

	interface View extends BaseView<Presenter> {
		void showTakenPictureActions();

		void displayEmojiActions(boolean showSelfieActions);

		void setCameraTextureViewSize(com.google.android.gms.common.images.Size camSize);

		void showCameraErrorDialog();
	}
}
