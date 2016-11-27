package com.marked.pixsee.camerasource;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.ViewGroup;

import static org.pixsee.renderer.Utils.isPortraitMode;

/**
 * Created by Tudor on 15-Aug-16.
 */

public class CameraTextureView extends TextureView {
	public CameraTextureView(Context context) {
		super(context);
	}

	public CameraTextureView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void setPreviewSize(Camera.Size size) {
		// Swap width and height sizes when in portrait, since it will be rotated 90 degrees
		int cameraPreviewWidth = size.width;
		int cameraPreviewHeight = size.height;
		if (isPortraitMode(getContext())) {
			int tmp = cameraPreviewWidth;
			//noinspection SuspiciousNameCombination
			cameraPreviewWidth = cameraPreviewHeight;
			cameraPreviewHeight = tmp;
		}
		// container of the camera surface and renderer surface
		ViewGroup previewContainer = (ViewGroup) getParent();

		final int viewWidth = previewContainer.getRight() - previewContainer.getLeft();
		final int viewHeight = previewContainer.getBottom() - previewContainer.getTop();


		int childWidth, childHeight;
		int childXOffset = 0, childYOffset = 0;
		float widthRatio = (float) viewWidth / (float) cameraPreviewWidth;
		float heightRatio = (float) viewHeight / (float) cameraPreviewHeight;

		// To fill the view with the camera preview, while also preserving the correct aspect ratio,
		// it is usually necessary to slightly oversize the child and to crop off portions along one
		// of the dimensions.  We scale up based on the dimension requiring the most correction, and
		// compute a crop offset for the other dimension.
		if (widthRatio > heightRatio) {
			childWidth = viewWidth;
			childHeight = (int) ((float) cameraPreviewHeight * widthRatio);
			childYOffset = (childHeight - viewHeight) / 2;
		} else {
			childWidth = (int) ((float) cameraPreviewWidth * heightRatio);
			childHeight = viewHeight;
			childXOffset = (childWidth - viewWidth) / 2;
		}
		for (int i = 0; i < previewContainer.getChildCount(); ++i) {
			// One dimension will be cropped.  We shift child over or up by this offset and adjust
			// the size to maintain the proper aspect ratio.
			previewContainer.getChildAt(i).layout(
					-1 * childXOffset,
					-1 * childYOffset,
					childWidth - childXOffset,
					childHeight - childYOffset);
		}
	}
}
