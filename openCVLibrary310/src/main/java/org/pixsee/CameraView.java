package org.pixsee;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import org.opencv.android.JavaCameraView;
import org.opencv.core.Size;

import java.io.FileOutputStream;
import java.util.List;

import static android.hardware.Camera.PictureCallback;

/**
 * Created by Tudor on 27-Nov-16.
 */

public class CameraView extends JavaCameraView implements PictureCallback {
	private static final String TAG = "Sample::CameraView";
	private String mPictureFileName;
	private Context mContext;

	public CameraView(Context context) {
		this(context, CAMERA_ID_FRONT);
	}

	public CameraView(Context context, int cameraId) {
		super(context, cameraId);
		mContext = context;
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs);
	}

	public List<String> getEffectList() {
		return mCamera.getParameters().getSupportedColorEffects();
	}

	public boolean isEffectSupported() {
		return (mCamera.getParameters().getColorEffect() != null);
	}

	public String getEffect() {
		return mCamera.getParameters().getColorEffect();
	}

	public void setEffect(String effect) {
		Camera.Parameters params = mCamera.getParameters();
		params.setColorEffect(effect);
		mCamera.setParameters(params);
	}

	public List<Camera.Size> getResolutionList() {
		return mCamera.getParameters().getSupportedPreviewSizes();
	}

	public Camera.Size getResolution() {
		return mCamera.getParameters().getPreviewSize();
	}

	public void setResolution(Camera.Size resolution) {
		disconnectCamera();
		mMaxHeight = resolution.height;
		mMaxWidth = resolution.width;
		connectCamera(getWidth(), getHeight());
	}

	public void takePicture(final String fileName) {
		Log.i(TAG, "Taking picture");
		this.mPictureFileName = fileName;
		// Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
		// Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
		mCamera.setPreviewCallback(null);

		// PictureCallback is implemented by the current class
		mCamera.takePicture(null, null, this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.i(TAG, "Saving a bitmap to file");
		// The camera preview was automatically stopped. Start it again.
		mCamera.startPreview();
		mCamera.setPreviewCallback(this);

		// Write the image in a file (in jpeg format)
		try {
			FileOutputStream fos = new FileOutputStream(mPictureFileName);

			fos.write(data);
			fos.close();

		} catch (java.io.IOException e) {
			Log.e("PictureDemo", "Exception in photoCallback", e);
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

		if (mPreviewSize == null) {
			setMeasuredDimension(width, height);
			return;
		}
		float ratio = calculateViewRatio();
		setMeasuredDimension(width, (int) (width * ratio));
	}

	@Override
	protected Size calculateCameraFrameSize(List<Camera.Size> supportedSizes, int surfaceWidth, int surfaceHeight) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) surfaceHeight / surfaceWidth;

		if (supportedSizes == null)
			return null;

		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = surfaceHeight;

		for (Camera.Size size : supportedSizes) {
			double ratio = (double) size.height / size.width;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;

			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : supportedSizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}

		return new Size(optimalSize.width, optimalSize.height);
	}
}
