package org.pixsee;

import android.content.Context;
import android.hardware.Camera;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.List;

/**
 * Created by Tudor on 27-Nov-16.
 */

public class CameraFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {
	private static final String TAG = CameraFragment.class + "***";
	protected CameraView mCameraView;
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getActivity()) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
				case LoaderCallbackInterface.SUCCESS: {
					Log.i(TAG, "OpenCV loaded successfully");
					mCameraView.enableView();

//					mCameraView.setOnTouchListener(SelfieFragment.this);
				}
				break;
				default: {
					super.onManagerConnected(status);
				}
				break;
			}
		}
	};

	public CameraFragment() {
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
//		mCameraView = new CameraView(getActivity(), CameraBridgeViewBase.CAMERA_ID_FRONT);
//		mCameraView.setCvCameraViewListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mCameraView != null)
			mCameraView.setCvCameraViewListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!OpenCVLoader.initDebug()) {
			Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, getActivity().getApplicationContext(), mLoaderCallback);
		} else {
			Log.d(TAG, "OpenCV library found inside package. Using it!");
			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// TODO: 27-Nov-16 initialisation happens in constructor. Check for null if initialisation is happening in xml
		mCameraView.disableView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {

	}

	@Override
	public void onCameraViewStopped() {

	}

	@Override
	public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
		return inputFrame.gray();
	}

	public Camera.Size getMaxResolution() {
		List<Camera.Size> sizes = mCameraView.getResolutionList();
		Camera.Size max = sizes.get(0);
		for (int i = 0; i < sizes.size(); i++) {
			if (sizes.get(i).height >= max.height && sizes.get(i).width >= max.width)
				max = sizes.get(i);
		}
		return max;
	}
}
