package com.marked.pixsee.face.di;

import android.hardware.Camera;
import android.util.Log;

import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.marked.pixsee.face.SelfieFragment;
import com.marked.pixsee.face.custom.CameraSource;
import com.marked.pixsee.face.FaceContract;
import com.marked.pixsee.face.FacePresenter;
import com.marked.pixsee.face.custom.FaceRenderer;
import com.marked.pixsee.face.custom.FaceTrackerAR;
import com.marked.pixsee.injection.scopes.PerActivity;

import java.lang.ref.WeakReference;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 4/24/2016.
 */
@Module
public class SelfieModule {
	private static final String TAG = "SELFIE_MODULE";
	private WeakReference<SelfieFragment> mSelfieFragment;

	public SelfieModule(SelfieFragment selfieFragment) {
		this.mSelfieFragment = new WeakReference<SelfieFragment>(selfieFragment);
	}

	@Provides
	@PerActivity
	public FaceContract.Presenter provideFacePresenter() {
		return new FacePresenter(mSelfieFragment.get());
	}

	@Provides
	@PerActivity
	public FaceRenderer provideFaceRenderer() {
		return new FaceRenderer(mSelfieFragment.get().getActivity());
	}

	@Provides
	public CameraSource provideCameraSource(FaceRenderer faceRenderer) {
		FaceDetector faceDetector = new FaceDetector.Builder(mSelfieFragment.get().getContext())
				                            .setTrackingEnabled(true)
				                            .setProminentFaceOnly(true)
				                            .setClassificationType(FaceDetector.FAST_MODE)
				                            .setLandmarkType(0)
				                            .build();
		FaceTrackerAR faceTracker = new FaceTrackerAR(faceRenderer);
		faceDetector.setProcessor(new LargestFaceFocusingProcessor.Builder(faceDetector, faceTracker).build());
		if (!faceDetector.isOperational()) {
			// Note: The first time that an app using face API is installed on a device, GMS will
			// download a native library to the device in order to do detection.  Usually this
			// completes before the app is run for the first time.  But if that download has not yet
			// completed, then the above call will not detect any faces.
			//
			// isOperational() can be used to check if the required native library is currently
			// available.  The faceDetector will automatically become operational once the library
			// download completes on device.
			Log.w(TAG, "Face faceDetector dependencies are not yet available.");
		}
		return new CameraSource.Builder(mSelfieFragment.get().getContext(), faceDetector).setRequestedFps(30.0f)
				       .setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO)
				       .setFacing(com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT)
				       .build();
	}


}
