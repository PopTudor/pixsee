package org.pixsee.renderer.transformation;

/**
 * Created by Tudor on 20-Aug-16.
 */

public abstract class Transform {
	protected static float mWidthScaleFactor = 1.0f, mHeightScaleFactor = 1.0f;
	protected static int sCurrentViewportWidth, sCurrentViewportHeight;

	public static void setCurrentViewport(int currentViewportWidth, int currentViewportHeight) {
		sCurrentViewportWidth = currentViewportWidth;
		sCurrentViewportHeight = currentViewportHeight;
	}

	public static void setScaleFactor(float widthScaleFactor, float heightScaleFactor) {
		mWidthScaleFactor = widthScaleFactor;
		mHeightScaleFactor = heightScaleFactor;
	}
//	public abstract void transform(Object3D object3D, PixseeFace pixseeFace);
}
