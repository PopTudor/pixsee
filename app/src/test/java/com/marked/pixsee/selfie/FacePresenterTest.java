package com.marked.pixsee.selfie;

import android.graphics.SurfaceTexture;

import com.marked.pixsee.selfie.camerasource.CameraSource;
import com.marked.pixsee.selfie.camerasource.PictureCallback;
import com.marked.pixsee.selfie.camerasource.ShutterCallback;
import com.marked.pixsee.selfie.renderer.SelfieRenderer;

import org.jetbrains.annotations.Contract;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Tudor on 12-Aug-16.
 */
public class FacePresenterTest {
	@Mock
	SelfieContract.View mView;
	@Mock
	SelfieRenderer mRenderer;
	@Mock
	CameraSource mCameraSource;

	private FacePresenter mFacePresenter;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		mFacePresenter = new FacePresenter(mView, mRenderer, mCameraSource);
		Mockito.doNothing().when(mCameraSource).stop();
	}

	@Test
	public void viewShouldHavePresenter() throws Exception {
		verify(mView).setPresenter(mFacePresenter);
	}

	@Test
	public void takePicture_shouldCallCameraSourceTakePicture() throws Exception {
		mFacePresenter.takePicture();

		verify(mView).displayEmojiActions(false);
		verify(mCameraSource).takePicture(any(ShutterCallback.class), any(PictureCallback.class));
	}

	@Test
	public void takePicture_shouldFreezeCamera() throws Exception {
		doAnswer(callTakePictureCallbacks()).when(mCameraSource).takePicture(any(ShutterCallback.class), any(PictureCallback.class));

		mFacePresenter.takePicture();

		verify(mCameraSource).stop();
	}

	@Test
	public void takePicture_shouldShowActions() throws Exception {
		doAnswer(callTakePictureCallbacks()).when(mCameraSource).takePicture(any(ShutterCallback.class), any(PictureCallback.class));

		mFacePresenter.takePicture();

		verify(mView).showTakenPictureActions();
	}

	@Test
	public void nullSurfaceTexture_shouldNotStartCamera() throws Exception {
		mFacePresenter.resumeSelfie();

		verify(mView).displayEmojiActions(true);
		verify(mCameraSource, never()).start(any(SurfaceTexture.class));
	}

	@Test
	public void release() throws Exception {
		mFacePresenter.release();

		verify(mCameraSource).release();
	}
	
	@Test
	public void startCamera_shouldChangeCameraSurfaceSize() throws Exception {
		mFacePresenter.onAvailableCameraSurfaceTexture(mock(SurfaceTexture.class),0,0);

		verify(mView).setCameraTextureViewSize(any(com.google.android.gms.common.images.Size.class));
	}
	
	/*****************
	 * DSL
	 *******************/
	@Contract(" -> !null")
	private Answer callTakePictureCallbacks() {
		return new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ShutterCallback shutterCallback = (ShutterCallback) invocation.getArguments()[0];
				PictureCallback pictureCallback = (PictureCallback) invocation.getArguments()[1];
				shutterCallback.onShutter();
				pictureCallback.onPictureTaken(new byte[]{});
				return null;
			}
		};
	}
}