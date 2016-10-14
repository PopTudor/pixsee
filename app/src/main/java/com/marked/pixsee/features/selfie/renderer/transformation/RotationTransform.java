package com.marked.pixsee.features.selfie.renderer.transformation;

import com.marked.pixsee.face.PixseeFace;

import org.rajawali3d.Object3D;
import org.rajawali3d.math.vector.Vector3;

/**
 * Created by Tudor on 20-Aug-16.
 */

public class RotationTransform extends Transform {
	/**
	 * Rotate the object based on pixseeFace rotation
	 *
	 * @param object3D the object to rotate
	 * @param pixseeFace     the pixseeFace to get the rotation from
	 */
	@Override
	public void transform(Object3D object3D, PixseeFace pixseeFace) {
		float eulerZ = pixseeFace.getEulerZ();
		object3D.rotateAround(Vector3.Y, eulerZ, false);
	}
}
