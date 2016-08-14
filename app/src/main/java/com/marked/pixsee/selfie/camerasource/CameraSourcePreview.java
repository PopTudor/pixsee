package com.marked.pixsee.selfie.camerasource;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;
import com.marked.pixsee.selfie.renderer.SelfieRenderer;

import org.rajawali3d.renderer.Renderer;

import java.io.IOException;

/**
 * Coordinate
 */
public class CameraSourcePreview extends ViewGroup {
	private static final String TAG = "CameraSourcePreview";

	private Context mContext;
	private CameraSource mCameraSource;
	private Renderer mSelfieRenderer;
	private SurfaceTexture surfaceTexture;

	public CameraSourcePreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	private void startIfReady() throws IOException {
		if (mSelfieRenderer != null) {
			Size size = mCameraSource.getPreviewSize();
			((SelfieRenderer) mSelfieRenderer).setCameraInfo(size, mCameraSource.getCameraFacing());
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