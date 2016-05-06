package com.marked.pixsee.face;

import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

/**
 * Created by Tudor on 5/6/2016.
 */
public class FaceTrackerAR extends Tracker<Face> {
	private static final String TAG = "FACE_TRACKER_***";
	private FaceRenderer mFaceRenderer;

	public FaceTrackerAR(FaceRenderer faceRenderer) {
		mFaceRenderer = faceRenderer;
	}

	@Override
	public void onNewItem(int id, Face item) {
		super.onNewItem(id, item);
		mFaceRenderer.onNewItem(item);
		Log.i(TAG, "Awesome person detected.  Hello!");
	}

	@Override
	public void onUpdate(Detector.Detections<Face> detections, Face item) {
		super.onUpdate(detections, item);
		if (item.getIsSmilingProbability() > 0.75) {
			Log.i(TAG, "I see a smile.  They must really enjoy your app.");
			mFaceRenderer.isSmiling();
		}
		mFaceRenderer.onUpdate(item);
	}

	@Override
	public void onMissing(Detector.Detections<Face> detections) {
		super.onMissing(detections);
	}

	@Override
	public void onDone() {
		super.onDone();
		mFaceRenderer.onDone();
		Log.i(TAG, "Elvis has left the building.");
	}
}
