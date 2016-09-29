package com.marked.pixsee.selfie.renderer.transformation;

import com.marked.pixsee.model.face.Face;

import org.rajawali3d.Object3D;

/**
 * Created by Tudor on 20-Aug-16.
 */

public class ScaleTransform extends Transform {

	/**
	 * Scale the object based on face size
	 *
	 * @param object3D the object to scale
	 * @param face     the face to scale upon
	 */
	@Override
	public void transform(Object3D object3D, Face face) {
		double dist = face.center();
		double scaleValue = dist / sCurrentViewportWidth; /* convert from pixels to normalized scale*/
		object3D.setScale(scaleValue);
	}
}
