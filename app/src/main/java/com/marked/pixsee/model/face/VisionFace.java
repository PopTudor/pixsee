package com.marked.pixsee.model.face;

import android.graphics.PointF;

/**
 * Created by Tudor on 29-Sep-16.
 */

public class VisionFace extends PixseeFace {
	com.google.android.gms.vision.face.Face mFace;

	public void setFace(com.google.android.gms.vision.face.Face face) {
		PointF xyCoord = face.getPosition();
		mLeft = xyCoord.x;
		mTop = xyCoord.y;
		mRight = face.getWidth();
		mBottom = face.getHeight();
		mEulerY = face.getEulerY();
		mEulerZ = face.getEulerZ();
	}
}
