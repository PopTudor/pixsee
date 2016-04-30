package com.marked.pixsee.face;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;

import java.io.IOException;

public class CameraSourcePreview extends ViewGroup {
	private static final String TAG = "CameraSourcePreview";

	private Context mContext;
	private TextureView mTextureView; /* We user TextureView because we have to mirror the camera preview on front camera*/
	private boolean mStartRequested;
	private boolean mSurfaceAvailable;
	private CameraSourcePixsee mCameraSource;

	private FaceRenderer mOverlay;

	public CameraSourcePreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mStartRequested = false;
		mSurfaceAvailable = false;

		setTextureView(new TextureView(context));
		addView(mTextureView);
	}

	void setTextureView(TextureView surfaceView) {
		mTextureView = surfaceView;
		mTextureView.setSurfaceTextureListener(new TextureCallback());
	}

	public TextureView getTextureView() {
		return mTextureView;
	}

	public void start(CameraSourcePixsee cameraSource) throws IOException {
		if (cameraSource == null) {
			stop();
		}

		mCameraSource = cameraSource;

		if (mCameraSource != null) {
			mStartRequested = true;
			startIfReady();
		}
	}

	public void start(CameraSourcePixsee cameraSource, FaceRenderer overlay) throws IOException {
		mOverlay = overlay;
		start(cameraSource);
	}

	public void stop() {
		if (mCameraSource != null) {
			mCameraSource.stop();
		}
	}

	public void release() {
		if (mCameraSource != null) {
			mCameraSource.release();
			mCameraSource = null;
		}
	}

	private void startIfReady() throws IOException {
		if (mStartRequested && mSurfaceAvailable) {
			if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			if (mCameraSource.getCameraFacing()==CameraSourcePixsee.CAMERA_FACING_FRONT) {
//				Matrix mirrorMatrix = new Matrix();
//				mirrorMatrix.preScale(-1, 1);
//				mTextureView.setTransform(mirrorMatrix);
//				mTextureView.setScaleX(-1);
			}
			mCameraSource.start(mTextureView.getSurfaceTexture());
			if (mOverlay != null) {
				Size size = mCameraSource.getPreviewSize();
				int min = Math.min(size.getWidth(), size.getHeight());
				int max = Math.max(size.getWidth(), size.getHeight());
				if (isPortraitMode()) {
					// Swap width and height sizes when in portrait, since it will be rotated by
					// 90 degrees
					mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
				} else {
					mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
				}
//				mOverlay.clear();
			}
			mStartRequested = false;
		}
	}

	private class TextureCallback implements TextureView.SurfaceTextureListener {
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			mSurfaceAvailable = true;

			try {
				startIfReady();
			} catch (IOException e) {
				Log.e(TAG, "Could not start camera source.", e);
			}
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			mSurfaceAvailable = false;
			return false;
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {

		}
	}

	private class SurfaceCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceCreated(SurfaceHolder surface) {
			mSurfaceAvailable = true;
			try {
				startIfReady();
			} catch (IOException e) {
				Log.e(TAG, "Could not start camera source.", e);
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder surface) {
			mSurfaceAvailable = false;
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int previewWidth = 320;
		int previewHeight = 240;
		if (mCameraSource != null) {
			Size size = mCameraSource.getPreviewSize();
			if (size != null) {
				previewWidth = size.getWidth();
				previewHeight = size.getHeight();
			}
		}

		// Swap width and height sizes when in portrait, since it will be rotated 90 degrees
		if (isPortraitMode()) {
			int tmp = previewWidth;
			previewWidth = previewHeight;
			previewHeight = tmp;
		}

		final int viewWidth = right - left;
		final int viewHeight = bottom - top;

		int childWidth;
		int childHeight;
		int childXOffset = 0;
		int childYOffset = 0;
		float widthRatio = (float) viewWidth / (float) previewWidth;
		float heightRatio = (float) viewHeight / (float) previewHeight;

		// To fill the view with the camera preview, while also preserving the correct aspect ratio,
		// it is usually necessary to slightly oversize the child and to crop off portions along one
		// of the dimensions.  We scale up based on the dimension requiring the most correction, and
		// compute a crop offset for the other dimension.
		if (widthRatio > heightRatio) {
			childWidth = viewWidth;
			childHeight = (int) ((float) previewHeight * widthRatio);
			childYOffset = (childHeight - viewHeight) / 2;
		} else {
			childWidth = (int) ((float) previewWidth * heightRatio);
			childHeight = viewHeight;
			childXOffset = (childWidth - viewWidth) / 2;
		}

		for (int i = 0; i < getChildCount(); ++i) {
			// One dimension will be cropped.  We shift child over or up by this offset and adjust
			// the size to maintain the proper aspect ratio.
			getChildAt(i).layout(
					-1 * childXOffset, -1 * childYOffset,
					childWidth - childXOffset, childHeight - childYOffset);
		}

		try {
			startIfReady();
		} catch (IOException e) {
			Log.e(TAG, "Could not start camera source.", e);
		}
	}

	private boolean isPortraitMode() {
		int orientation = mContext.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return false;
		}
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			return true;
		}

		Log.d(TAG, "isPortraitMode returning false by default");
		return false;
	}
}