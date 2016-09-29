package com.marked.pixsee.selfie.renderer.transformation;

import com.google.android.gms.vision.CameraSource;
import com.marked.pixsee.model.face.Face;

import org.rajawali3d.Object3D;

/**
 * Created by Tudor on 20-Aug-16.
 */

public class TranslateTransform extends Transform {
	public static int mFacing;
	private static float mWidthScaleFactor = 1.0f, mHeightScaleFactor = 1.0f;
	private int CAMERA_Z;

	public TranslateTransform(int CAMERA_Z) {
		this.CAMERA_Z = CAMERA_Z;
		mFacing = CameraSource.CAMERA_FACING_FRONT;
	}

	public static void setScaleFactor(float widthScaleFactor, float heightScaleFactor) {
		TranslateTransform.mWidthScaleFactor = widthScaleFactor;
		TranslateTransform.mHeightScaleFactor = heightScaleFactor;
	}

	/**
	 * Translate the object based on face location
	 *
	 * @param object3D the object to translate
	 * @param face     the face to get it's position
	 */
	@Override
	public void transform(Object3D object3D, Face face) {
		float x = translateX(face.centerX());
		float y = translateY(face.centerY());
		object3D.setScreenCoordinates(x, y, sCurrentViewportWidth, sCurrentViewportHeight, CAMERA_Z);
	}

	/**
	 * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
	 * system.
	 */
	private float translateY(float y) {
		if (mFacing == CameraSource.CAMERA_FACING_FRONT)
			return sCurrentViewportHeight - scaleY(y);
		else
			return scaleY(y);
	}

	/**
	 * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
	 * system.
	 */
	private float translateX(float x) {
		if (mFacing == CameraSource.CAMERA_FACING_FRONT) {
			return sCurrentViewportWidth - scaleX(x);
		} else {
			return scaleX(x);
		}
	}

	/**
	 * Adjusts a horizontal value of the supplied value from the preview scale to the view
	 * scale.
	 */
	private float scaleX(float horizontal) {
		return horizontal * mWidthScaleFactor;
	}

	/**
	 * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
	 */
	private float scaleY(float vertical) {
		return vertical * mHeightScaleFactor;
	}

}
