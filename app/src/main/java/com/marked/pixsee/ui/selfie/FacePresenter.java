package com.marked.pixsee.ui.selfie;

import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;

import com.marked.pixsee.camerasource.PixseeCamera;
import com.marked.pixsee.ui.commands.Command;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-10.
 */
class FacePresenter implements SelfieContract.Presenter {
	private final PixseeCamera cameraSource;
	private WeakReference<SelfieContract.View> mView;
	private SurfaceTexture mCameraSurfaceTexture;

	FacePresenter(@NonNull SelfieContract.View view, @NonNull PixseeCamera cameraSource) {
		this.mView = new WeakReference<>(view);
		this.cameraSource = cameraSource;
		this.mView.get().setPresenter(this);
	}

	@Override
	public void takePicture() {
		mView.get().displayEmojiActions(false);
//		cameraSource.takePicture(new ShutterCallback() {
//			@Override
//			public void onShutter() {
//				mView.get().showTakenPictureActions(); /* when the user touches the selfie button */
//			}
//		}, new PictureCallback() {
//			@Override
//			public void onPictureTaken(byte[] data) {
//				cameraSource.stop();
//			}
//		});
	}

	@Override
	public void resumeSelfie() {
		mView.get().displayEmojiActions(true);
		startCamera();
	}

	@Override
	public void onAvailableCameraSurfaceTexture(SurfaceTexture cameraSurfaceTexture, int width, int height) {
		mCameraSurfaceTexture = cameraSurfaceTexture;
		startCamera();
	}

	private void startCamera() {
//		try {
			if (mCameraSurfaceTexture != null) {
//				cameraSource.start(mCameraSurfaceTexture);
				setPreviewSizes();
			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (RuntimeException cameraNotAvailable) {
//			mView.get().showCameraErrorDialog();
//		}
	}

	private void setPreviewSizes() {
//		Size size = cameraSource.getPreviewSize();
//		((SelfieRenderer) renderer).setCameraInfo(size, cameraSource.getCameraFacing());
		// cameraTextureView preview size
//		mView.get().setCameraTextureViewSize(size);
	}

	@Override
	public void release() {
//		cameraSource.release();
	}

	@Override
	public void execute(Command command) {
		command.execute();
	}

	@Override
	public void attach() {

	}

	@Override
	public void detach() {

	}
}
