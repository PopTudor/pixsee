package com.marked.pixsee.selfie.renderer.transformation;

import com.marked.pixsee.model.face.Face;

import org.rajawali3d.Object3D;

/**
 * Created by Tudor on 20-Aug-16.
 */

public abstract class Transform {
	protected static int sCurrentViewportWidth, sCurrentViewportHeight;

	public static void setCurrentViewport(int currentViewportWidth, int currentViewportHeight) {
		sCurrentViewportWidth = currentViewportWidth;
		sCurrentViewportHeight = currentViewportHeight;
	}

	public abstract void transform(Object3D object3D, Face face);
}
