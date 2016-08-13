package com.marked.pixsee.selfie;

import com.marked.pixsee.commands.Command;
import com.marked.pixsee.selfie.custom.CameraSource;

import org.jetbrains.annotations.NotNull;
import org.rajawali3d.renderer.Renderer;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-10.
 */
class FacePresenter implements SelfieContract.Presenter {
	private final CameraSource cameraSource;
	private WeakReference<SelfieContract.View> mView;
	private Renderer renderer;
	private CameraSource mCameraSource;

	FacePresenter(@NotNull SelfieContract.View view, @NotNull Renderer renderer, @NotNull CameraSource cameraSource) {
		this.mView = new WeakReference<>(view);
		this.renderer = renderer;
		this.cameraSource = cameraSource;
		this.mView.get().setPresenter(this);
	}

	@Override
	public void takePicture() {
		mView.get().displayEmojiActions(false);
		cameraSource.takePicture(new CameraSource.ShutterCallback() {
			@Override
			public void onShutter() {
				mView.get().showTakenPictureActions(); /* when the user touches the selfie button */
			}
		}, new CameraSource.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data) {
				mView.get().stopCamera();
			}
		});
	}

	@Override
	public void resumeSelfie() {
		mView.get().displayEmojiActions(true);
		mView.get().startCamera(cameraSource, renderer);
	}

	@Override
	public void execute(Command command) {
		command.execute();
	}

	@Override
	public void attach() {

	}
}
