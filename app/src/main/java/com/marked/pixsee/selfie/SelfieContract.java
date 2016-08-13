package com.marked.pixsee.selfie;

import android.graphics.SurfaceTexture;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.selfie.custom.CameraSource;

import org.rajawali3d.renderer.Renderer;

/**
 * Created by Tudor on 2016-05-22.
 */
class SelfieContract {
	interface Presenter extends BasePresenter {
		void execute(Command command);

		void takePicture();

		void resumeSelfie();
	}

	interface View extends BaseView<Presenter> {
		void showTakenPictureActions();

		void displayEmojiActions(boolean showSelfieActions);

		void setSurfaceTexture(SurfaceTexture texture);

		void startCamera(CameraSource source, Renderer renderer);

		void stopCamera();
	}
}
