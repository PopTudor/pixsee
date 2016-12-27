package com.marked.pixsee.camerasource.face;

import android.graphics.PointF;

/**
 * Created by Tudor on 29-Sep-16.
 */

public class VisionFace extends PixseeFace {
	com.google.android.gms.vision.face.Face mFace;

	public void setFace(com.google.android.gms.vision.face.Face face) {
		mFace = face;
		PointF xyCoord = face.getPosition();
		mPositionX = xyCoord.x;
		mPositionY = xyCoord.y;
		mWidth = face.getWidth();
		mHeight = face.getHeight();
		mEulerY = face.getEulerY();
		mEulerZ = face.getEulerZ();
	}
}
