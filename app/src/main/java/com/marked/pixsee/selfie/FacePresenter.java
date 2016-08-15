package com.marked.pixsee.selfie;

import android.graphics.SurfaceTexture;

import com.google.android.gms.common.images.Size;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.selfie.camerasource.CameraSource;
import com.marked.pixsee.selfie.camerasource.PictureCallback;
import com.marked.pixsee.selfie.camerasource.ShutterCallback;
import com.marked.pixsee.selfie.renderer.SelfieRenderer;

import org.jetbrains.annotations.NotNull;
import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.renderer.Renderer;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 2016-05-10.
 */
class FacePresenter implements SelfieContract.Presenter {
	private final CameraSource cameraSource;
	private WeakReference<SelfieContract.View> mView;
	private Renderer renderer;
	private SurfaceTexture mCameraSurfaceTexture;

	FacePresenter(@NotNull SelfieContract.View view, @NotNull Renderer renderer, @NotNull CameraSource cameraSource) {
		this.mView = new WeakReference<>(view);
		this.renderer = renderer;
		this.cameraSource = cameraSource;
		this.mView.get().setPresenter(this);
	}

	@Override
	public void takePicture() {
		mView.get().displayEmojiActions(false);
		cameraSource.takePicture(new ShutterCallback() {
			@Override
			public void onShutter() {
				mView.get().showTakenPictureActions(); /* when the user touches the selfie button */
			}
		}, new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data) {
				cameraSource.stop();
			}
		});
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

	private void startCamera(){
		try {
			if (mCameraSurfaceTexture!=null) {
				cameraSource.start(mCameraSurfaceTexture);
				setPreviewSizes();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setPreviewSizes() {
		Size size = cameraSource.getPreviewSize();
		// render preview size
		((SelfieRenderer) renderer).setCameraInfo(size, cameraSource.getCameraFacing());
		// cameraTextureView preview size
		mView.get().setCameraTextureViewSize(size);
	}

	@Override
	public void release() {
		cameraSource.release();
	}

	@Override
	public ISurfaceRenderer getRenderer() {
		return renderer;
	}

	@Override
	public void execute(Command command) {
		command.execute();
	}

	@Override
	public void attach() {

	}

}
