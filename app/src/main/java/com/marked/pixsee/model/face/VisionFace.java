package com.marked.pixsee.model.face;

/**
 * Created by Tudor on 29-Sep-16.
 */

public class VisionFace extends Face {
	com.google.android.gms.vision.face.Face mFace;

	public VisionFace(com.google.android.gms.vision.face.Face face) {
		mFace = face;
	}
}
