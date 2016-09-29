package com.marked.pixsee.selfie.renderer.transformation;

import com.marked.pixsee.model.face.PixseeFace;

import org.rajawali3d.Object3D;

/**
 * Created by Tudor on 20-Aug-16.
 */

public class ScaleTransform extends com.marked.pixsee.selfie.renderer.transformation.Transform {

	/**
	 * Scale the object based on pixseeFace size
	 *
	 * @param object3D the object to scale
	 * @param pixseeFace     the pixseeFace to scale upon
	 */
	@Override
	public void transform(Object3D object3D, PixseeFace pixseeFace) {
		double dist = pixseeFace.distance();
		double scaleValue = dist / sCurrentViewportWidth; /* convert from pixels to normalized scale*/
		object3D.setScale(scaleValue);
	}
}
