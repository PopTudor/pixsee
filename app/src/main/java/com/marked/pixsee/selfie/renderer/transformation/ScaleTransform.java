package com.marked.pixsee.selfie.renderer.transformation;

import android.support.annotation.NonNull;

import com.google.android.gms.vision.face.Face;
import com.tzutalin.dlib.VisionDetRet;

import org.rajawali3d.Object3D;

/**
 * Created by Tudor on 20-Aug-16.
 */

public class ScaleTransform extends Transform {

	private static double calculateFaceCenter(Face face) {
		float x1 = face.getPosition().x;
		float y1 = face.getPosition().y;
		float x2 = face.getWidth();
		float y2 = face.getHeight();
		return Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0));
	}

	private static double calculateFaceCenter(VisionDetRet face) {
		float x1 = face.getLeft();
		float y1 = face.getTop();
		float x2 = face.getRight();
		float y2 = face.getBottom();
		return Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0));
	}

	/**
	 * Scale the object based on face size
	 *
	 * @param object3D the object to scale
	 * @param face     the face to scale upon
	 */
	@Override
	public void transform(Object3D object3D, Face face) {
		double dist = calculateFaceCenter(face);
		double scaleValue = dist / sCurrentViewportWidth; /* convert from pixels to normalized scale*/
		object3D.setScale(scaleValue);
	}

	private void scale(Object3D object3D, VisionDetRet ret) {
//		double dist = Utils.calculateFaceCenter(ret);
//		double scaleValue = dist / mDefaultViewportWidth; /* convert from pixels to normalized scale*/
//		object3D.setScale(scaleValue);
	}

	private void scale(@NonNull Object3D object3D, @NonNull Face face) {

	}
}
