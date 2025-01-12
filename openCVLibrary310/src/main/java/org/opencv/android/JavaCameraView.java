package org.opencv.android;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import static java.lang.Integer.valueOf;

/**
 * This class is an implementation of the Bridge View between OpenCV and Java Camera.
 * This class relays on the functionality available in base class and only implements
 * required functions:
 * connectCamera - opens Java camera and sets the PreviewCallback to be delivered.
 * disconnectCamera - closes the camera and stops preview.
 * When frame is delivered via callback from Camera - it processed via OpenCV to be
 * converted to RGBA32 and then passed to the external callback for modifications if required.
 */
public class JavaCameraView extends CameraBridgeViewBase implements PreviewCallback {
	private static final int MAGIC_TEXTURE_ID = 10;
	private static final String TAG = "JavaCameraView";
	private byte mBuffer[];
	private Mat[] mFrameChain;
	private int mChainIdx = 0;
	private Thread mThread;
	private boolean mStopThread;
	private SurfaceTexture mSurfaceTexture;
	private boolean mCameraFrameReady = false;
	private int mRotation;
	protected Camera mCamera;
	protected JavaCameraFrame[] mCameraFrame;
	protected Size mPreviewSize;
	protected List<Camera.Size> mPreviewSizes;
	protected Context mContext;

	public JavaCameraView(Context context, int cameraId) {
		super(context, cameraId);
		mContext = context;
	}

	public JavaCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	/**
	 * Calculates the correct rotation for the given camera id and sets the rotation in the
	 * parameters.  It also sets the camera's display orientation and rotation.
	 *
	 * @param parameters the camera parameters for which to set the rotation
	 * @param cameraId   the camera id to set rotation based on
	 */
	private void setRotation(Camera camera, Camera.Parameters parameters, int cameraId) {
		if (mContext == null)
			return;
		WindowManager windowManager =
				(WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		int degrees = 0;
		int rotation = windowManager.getDefaultDisplay().getRotation();
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			default:
				Log.e(TAG, "Bad rotation value: " + rotation);
		}

		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, cameraInfo);

		int angle;
		int displayAngle;
		if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			angle = (cameraInfo.orientation + degrees) % 360;
			displayAngle = (360 - angle); // compensate for it being mirrored
		} else {  // back-facing
			angle = (cameraInfo.orientation - degrees + 360) % 360;
			displayAngle = angle;
		}

		// This corresponds to the rotation constants in {@link Frame}.
		mRotation = angle / 90;

		camera.setDisplayOrientation(displayAngle);
		parameters.setRotation(angle);
		camera.setParameters(parameters);
	}

	protected boolean initializeCamera(int width, int height) {
		Log.d(TAG, "Initialize java camera");
		boolean result = true;
		synchronized (this) {
			mCamera = null;

			acquireCamera();

			if (mCamera == null)
				return false;
			setRotation(mCamera, mCamera.getParameters(), mCameraIndex);

            /* Now set camera parameters */
			try {
				Log.d(TAG, "getSupportedPreviewSizes()");
				Camera.Parameters params = mCamera.getParameters();
				mPreviewSizes = params.getSupportedPreviewSizes();
				if (mPreviewSizes != null) {
				    /* Select the size that fits surface considering maximum size allowed */
					mPreviewSize = calculateCameraFrameSize(mPreviewSizes, width, height);
					params.setPreviewFormat(ImageFormat.NV21);
					params.setPreviewSize((int) mPreviewSize.width, (int) mPreviewSize.height);
					Log.d(TAG, "Set preview size to " + valueOf((int) mPreviewSize.width) + "x" + valueOf((int) mPreviewSize.height));

					mScale = calculateViewRatio();

					setupRecordingHint(params);
					setupFocus(params);

					mCamera.setParameters(params);
					params = mCamera.getParameters();

					mFrameWidth = params.getPreviewSize().width;
					mFrameHeight = params.getPreviewSize().height;

					if (mFpsMeter != null) {
						mFpsMeter.setResolution(mFrameWidth, mFrameHeight);
					}

					int size = mFrameWidth * mFrameHeight;
					size = size * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
					mBuffer = new byte[size];

					mCamera.addCallbackBuffer(mBuffer);
					mCamera.setPreviewCallbackWithBuffer(this);

					mFrameChain = new Mat[2];
					mFrameChain[0] = new Mat(mFrameHeight + (mFrameHeight / 2), mFrameWidth, CvType.CV_8UC1);
					mFrameChain[1] = new Mat(mFrameHeight + (mFrameHeight / 2), mFrameWidth, CvType.CV_8UC1);

					AllocateCache();

					mCameraFrame = new JavaCameraFrame[2];
					mCameraFrame[0] = new JavaCameraFrame(mFrameChain[0], mFrameWidth, mFrameHeight);
					mCameraFrame[1] = new JavaCameraFrame(mFrameChain[1], mFrameWidth, mFrameHeight);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						mSurfaceTexture = new SurfaceTexture(MAGIC_TEXTURE_ID);
						mCamera.setPreviewTexture(mSurfaceTexture);
						mCamera.setDisplayOrientation(90);
					}

                    /* Finally we are ready to start the preview */
					Log.d(TAG, "startPreview");
					mCamera.startPreview();
				} else
					result = false;
			} catch (Exception e) {
				result = false;
				e.printStackTrace();
			}
		}

		return result;
	}

	protected float calculateViewRatio() {
		float scale;
		if (mPreviewSize.height >= mPreviewSize.width)
			scale = (float) mPreviewSize.height / (float) mPreviewSize.width;
		else
			scale = (float) mPreviewSize.width / (float) mPreviewSize.height;
		return scale;
	}

	private void acquireCamera() {
		if (mCameraIndex == CAMERA_ID_ANY) { // Build.VERSION.SDK_INT < Gingerbread
			openGingerbreadAnyCamera();
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			openSpecifiedCamera();
		}
	}

	private void setupRecordingHint(Camera.Parameters params) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && !Build.MODEL.equals("GT-I9100"))
			params.setRecordingHint(true);
	}

	private void setupFocus(Camera.Parameters params) {
		List<String> FocusModes = params.getSupportedFocusModes();
		if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		}
	}

	private void openSpecifiedCamera() {
		int localCameraIndex = mCameraIndex;
		if (mCameraIndex == CAMERA_ID_BACK) {
			Log.i(TAG, "Trying to open back camera");
			localCameraIndex = findCameraIndex(Camera.CameraInfo.CAMERA_FACING_BACK);
		} else if (mCameraIndex == CAMERA_ID_FRONT) {
			Log.i(TAG, "Trying to open front camera");
			localCameraIndex = findCameraIndex(Camera.CameraInfo.CAMERA_FACING_FRONT);
		}
		if (localCameraIndex == CAMERA_ID_BACK) {
			Log.e(TAG, "Back camera not found!");
		} else if (localCameraIndex == CAMERA_ID_FRONT) {
			Log.e(TAG, "Front camera not found!");
		} else {
			Log.d(TAG, "Trying to open camera with new open(" + valueOf(localCameraIndex) + ")");
			try {
				mCamera = Camera.open(localCameraIndex);
				mCameraIndex = localCameraIndex;
			} catch (RuntimeException e) {
				Log.e(TAG, "Camera #" + localCameraIndex + "failed to open: " + e.getLocalizedMessage());
				mCameraIndex = CAMERA_ID_FRONT;
			}
		}
	}

	private void openGingerbreadAnyCamera() {
		Log.d(TAG, "Trying to open camera with old open()");
		try {
			mCamera = Camera.open(); // try to open back-facing camera; return null if one is not present
		} catch (Exception e) {
			Log.e(TAG, "Camera is not available (in use or does not exist): " + e.getLocalizedMessage());
		}

		if (mCamera == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			boolean connected = false;
			for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
				Log.d(TAG, "Trying to open camera with new open(" + valueOf(camIdx) + ")");
				try {
					mCamera = Camera.open(camIdx);
					connected = true;
				} catch (RuntimeException e) {
					Log.e(TAG, "Camera #" + camIdx + "failed to open: " + e.getLocalizedMessage());
				}
				if (connected) break;
			}
		}
	}

	private int findCameraIndex(int cameraIdFront) {
		int localCameraIndex = CAMERA_ID_ANY;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == cameraIdFront) {
				localCameraIndex = camIdx;
				break;
			}
		}
		return localCameraIndex;
	}

	protected void releaseCamera() {
		synchronized (this) {
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.setPreviewCallback(null);

				mCamera.release();
			}
			mCamera = null;
			if (mFrameChain != null) {
				mFrameChain[0].release();
				mFrameChain[1].release();
			}
			if (mCameraFrame != null) {
				mCameraFrame[0].release();
				mCameraFrame[1].release();
			}
		}
	}

	@Override
	protected boolean connectCamera(int width, int height) {

        /* 1. We need to instantiate camera
         * 2. We need to start thread which will be getting frames
         */
        /* First step - initialize camera connection */
		Log.d(TAG, "Connecting to camera");
		if (!initializeCamera(width, height))
			return false;

		mCameraFrameReady = false;

        /* now we can start update thread */
		Log.d(TAG, "Starting processing thread");
		mStopThread = false;
		mThread = new Thread(new CameraWorker());
		mThread.start();

		return true;
	}

	@Override
	protected void disconnectCamera() {
        /* 1. We need to stop thread which updating the frames
         * 2. Stop camera and release it
         */
		Log.d(TAG, "Disconnecting from camera");
		try {
			mStopThread = true;
			Log.d(TAG, "Notify thread");
			synchronized (this) {
				this.notify();
			}
			Log.d(TAG, "Wating for thread");
			if (mThread != null)
				mThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			mThread = null;
		}

        /* Now release camera */
		releaseCamera();

		mCameraFrameReady = false;
	}

	@Override
	public void onPreviewFrame(byte[] frame, Camera arg1) {
		Log.d(TAG, "Preview Frame received. Frame size: " + frame.length);
		synchronized (this) {
			mFrameChain[mChainIdx].put(0, 0, frame);
			mCameraFrameReady = true;
			this.notify();
		}
		if (mCamera != null)
			mCamera.addCallbackBuffer(mBuffer);
	}

	private class JavaCameraFrame implements CvCameraViewFrame {
		private Mat mYuvFrameData;
		private Mat mRgba;
		private int mWidth;
		private int mHeight;

		public JavaCameraFrame(Mat Yuv420sp, int width, int height) {
			super();
			mWidth = width;
			mHeight = height;
			mYuvFrameData = Yuv420sp;
			mRgba = new Mat();
		}

		@Override
		public Mat gray() {
			return mYuvFrameData.submat(0, mHeight, 0, mWidth);
		}

		@Override
		public Mat rgba() {
			Imgproc.cvtColor(mYuvFrameData, mRgba, Imgproc.COLOR_YUV2RGBA_NV21, 4);
			return mRgba;
		}

		public void release() {
			mRgba.release();
		}
	}

	private class CameraWorker implements Runnable {

		@Override
		public void run() {
			do {
				boolean hasFrame = false;
				synchronized (JavaCameraView.this) {
					try {
						while (!mCameraFrameReady && !mStopThread) {
							JavaCameraView.this.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (mCameraFrameReady) {
						mChainIdx = 1 - mChainIdx;
						mCameraFrameReady = false;
						hasFrame = true;
					}
				}

				if (!mStopThread && hasFrame) {
					if (!mFrameChain[1 - mChainIdx].empty())
						deliverAndDrawFrame(mCameraFrame[1 - mChainIdx]);
				}
			} while (!mStopThread);
			Log.d(TAG, "Finish processing thread");
		}
	}

}
