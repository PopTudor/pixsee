package com.marked.pixsee.selfie.renderer.transformation;

import com.google.android.gms.vision.face.Face;

import org.rajawali3d.Object3D;
import org.rajawali3d.math.vector.Vector3;

/**
 * Created by Tudor on 20-Aug-16.
 */

public class RotationTransform extends Transform {
	/**
	 * Rotate the object based on face rotation
	 *
	 * @param object3D the object to rotate
	 * @param face     the face to get the rotation from
	 */
	@Override
	public void transform(Object3D object3D, Face face) {
		float eulerZ = face.getEulerZ();
		object3D.rotateAround(Vector3.Y, eulerZ, false);
	}
}
